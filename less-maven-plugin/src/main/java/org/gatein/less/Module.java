package org.gatein.less;

public class Module
{
   private String name;
   
   private String home;
   
   private String output;

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public String getHomeDirectory()
   {
      return home;
   }

   public void setHomeDirectory(String home)
   {
      this.home = home;
   }

   public String getOutput()
   {
      return output;
   }

   public void setOutput(String output)
   {
      this.output = output;
   }
}
