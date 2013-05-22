/*
 * Copyright (C) 2011 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.exoplatform.portal.config.model;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.exoplatform.portal.mop.Visibility;
import org.gatein.common.io.IOTools;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.impl.UnmarshallingContext;
import org.staxnav.Naming;
import org.staxnav.StaxNavigator;
import org.staxnav.StaxNavigatorFactory;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 */
public class ModelUnmarshaller {

    public static <T> UnmarshalledObject<T> unmarshall(Class<T> type, InputStream in) throws Exception {
        return unmarshall(type, IOTools.getBytes(in));
    }

    public static <T> UnmarshalledObject<T> unmarshall(Class<T> type, byte[] bytes) throws Exception {
        ByteArrayInputStream baos = new ByteArrayInputStream(bytes);

        // Find out version
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader = factory.createXMLStreamReader(baos);
        Version version = Version.UNKNOWN;
        while (reader.hasNext()) {
            int next = reader.next();
            if (next == XMLStreamReader.START_ELEMENT) {
                QName name = reader.getName();
                String uri = name.getNamespaceURI();
                if (uri != null) {
                    version = Version.forURI(uri);
                }
                break;
            }
        }

        //
        baos.reset();
        T obj;
        if (type == PageNavigation.class && version == Version.V_2_0) {
            obj = type.cast(parse(baos));
        } else {
            IBindingFactory bfact = BindingDirectory.getFactory(type);
            UnmarshallingContext uctx = (UnmarshallingContext) bfact.createUnmarshallingContext();
            uctx.setDocument(baos, null, "UTF-8", false);
            obj = type.cast(uctx.unmarshalElement());
        }

        //
        return new UnmarshalledObject<T>(version, obj);
    }

    enum Element {

        navigation, node, name, parent_uri, label, icon, start_publication_date, end_publication_date, visibility, page_reference

    }

    /** . */
    private static final QName LANG = new QName("http://www.w3.org/XML/1998/namespace", "lang");

    /** . */
    private static final Pattern RFC1766_PATTERN = Pattern.compile("^([a-zA-Z]{2})(?:-([a-zA-Z]{2}))?$");

    /** . */
    private static final ThreadLocal<DateFormat> dateFormat = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat();
        }
    };

    //
    private static PageNavigation parse(InputStream in) throws XMLStreamException, ParseException {

        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        inputFactory.setProperty("javax.xml.stream.isCoalescing", true);
        XMLStreamReader stream = inputFactory.createXMLStreamReader(in);
        StaxNavigator<Element> nav = StaxNavigatorFactory.create(new Naming.Enumerated.Simple<Element>(Element.class, null), stream);
        nav.setTrimContent(true);
        assert Element.navigation == nav.getName();

        //
        PageNavigation navigation = new PageNavigation();
        if (nav.child(Element.node)) {
            for (StaxNavigator<Element> portletNav : nav.fork(Element.node)) {
                PageNode root = parseNode(portletNav);
                NavigationFragment fragment = new NavigationFragment();
                fragment.setParentURI(root.getName());
                fragment.setState(root.getState());
                fragment.setNodes(root.getNodes());
                fragment.setLabels(root.getLabels());
                navigation.addFragment(fragment);
            }
        }

        //
        return navigation;
    }

    //
    private static PageNode parseNode(StaxNavigator<Element> nav) throws ParseException {
        PageNode node = new PageNode();
        for (Element child = nav.child();child != null;child = nav.sibling()) {
            switch (child) {
                case name:
                case parent_uri:
                    node.setName(nav.getContent());
                    break;
                case label:
                    String lang = nav.getAttribute(LANG);
                    String value = nav.getContent();

                    Locale locale = null;
                    if (lang != null) {
                        Matcher matcher = RFC1766_PATTERN.matcher(lang);
                        if (matcher.matches()) {
                            String langISO = matcher.group(1);
                            String countryISO = matcher.group(2);
                            if (countryISO == null) {
                                locale = new Locale(langISO.toLowerCase());
                            } else {
                                locale = new Locale(langISO.toLowerCase(), countryISO.toLowerCase());
                            }
                        } else {
                            throw new RuntimeException("The attribute xml:lang " + lang
                                    + " does not represent a valid language as defined by RFC 1766");
                        }
                    }
                    I18NString labels = node.getLabels();
                    if (labels == null) {
                        node.setLabels(labels = new I18NString());
                    }
                    labels.add(new LocalizedString(value, locale));
                    break;
                case icon:
                    node.setIcon(nav.getContent());
                    break;
                case start_publication_date:
                    node.setStartPublicationDate(dateFormat.get().parse(nav.getContent()));
                    break;
                case end_publication_date:
                    node.setEndPublicationDate(dateFormat.get().parse(nav.getContent()));
                    break;
                case visibility:
                    node.setVisibility(Visibility.valueOf(nav.getContent()));
                    break;
                case page_reference:
                    node.setPageReference(nav.getContent());
                    break;
                case node:
                    PageNode childNode = parseNode(nav.fork());
                    node.getChildren().add(childNode);
                    break;
            }
        }
        return node;
    }

}
