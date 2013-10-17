/*
 * Copyright (C) 2012 eXo Platform SAS.
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

package org.gatein.portal.mop.layout;

import org.gatein.portal.mop.hierarchy.GenericScope;
import org.gatein.portal.mop.hierarchy.HierarchyException;
import org.gatein.portal.mop.hierarchy.ModelAdapter;
import org.gatein.portal.mop.hierarchy.NodeChangeListener;
import org.gatein.portal.mop.hierarchy.NodeContext;
import org.gatein.portal.mop.hierarchy.NodeManager;
import org.gatein.portal.mop.hierarchy.NodeModel;
import org.gatein.portal.mop.hierarchy.Scope;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 */
public class LayoutServiceImpl implements LayoutService {

    /** . */
    private final Scope<ElementState> ALL = GenericScope.treeShape(-1);

    /** . */
    private final LayoutStore persistence;

    public LayoutServiceImpl(LayoutStore persistence) {
        if (persistence == null) {
            throw new NullPointerException("No null persistence provided");
        }

        //
        this.persistence = persistence;
    }

    private NodeManager<ElementState> getManager(String rootId, boolean write) {
        return new NodeManager<ElementState>(persistence.begin(rootId, write));
    }

    private NodeManager<ElementState> getManager(NodeContext<?, ElementState> root, boolean write) {
        return new NodeManager<ElementState>(persistence.begin(root.getId(), write));
    }

    @Override
    public <N> void saveLayout(ModelAdapter<N, ElementState> adapter, N node, NodeContext<N, ElementState> context, NodeChangeListener<NodeContext<N, ElementState>, ElementState> listener) throws NullPointerException, LayoutServiceException, HierarchyException {
        NodeManager<ElementState> nodeManager = getManager(context, true);
        try {
            // Make a diff
            nodeManager.diff(adapter, node, context);
            // Perform save
            nodeManager.saveNode(context, listener);
        } finally {
            persistence.end(nodeManager.getStore());
        }
    }
    
    @Override
    public <N> void rebaseLayout(ModelAdapter<N, ElementState> adapter, N node, NodeContext<N, ElementState> context, NodeChangeListener<NodeContext<N, ElementState>, ElementState> listener) throws NullPointerException, LayoutServiceException, HierarchyException {
        NodeManager<ElementState> nodeManager = getManager(context, true);
        try {
            // Make a diff
            nodeManager.diff(adapter, node, context);
            // Perform rebase
            nodeManager.rebaseNode(context, ALL, listener);
        } finally {
            persistence.end(nodeManager.getStore());
        }
    }

    @Override
    public <N> NodeContext<N, ElementState> loadLayout(NodeModel<N, ElementState> model, String layoutId, NodeChangeListener<NodeContext<N, ElementState>, ElementState> listener) {
        if (model == null) {
            throw new NullPointerException("No nullmodel accepted");
        }
        if (layoutId == null) {
            throw new NullPointerException("No null layout id accepted");
        }

        //
        NodeManager<ElementState> nodeManager = getManager(layoutId, false);

        //
        return nodeManager.loadNode(model, layoutId, ALL, listener);
    }

    @Override
    public <N> void saveLayout(NodeContext<N, ElementState> context, NodeChangeListener<NodeContext<N, ElementState>, ElementState> listener) throws NullPointerException {
        NodeManager<ElementState> nodeManager = getManager(context, true);
        try {
            nodeManager.saveNode(context, listener);
        } finally {
            persistence.end(nodeManager.getStore());
        }
    }
}
