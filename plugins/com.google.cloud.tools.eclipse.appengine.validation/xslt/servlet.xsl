<?xml version="1.0" encoding="UTF-8"?>
<!--
	This stylesheet changes the servlet in web.xml to servlet 2.5.
-->
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:template match="node()|@*">
    <xsl:copy>
      <xsl:apply-templates select="node()|@*"/>
    </xsl:copy>
  </xsl:template>
   
  <xsl:template match="/*">
  	<web-app xmlns="http://java.sun.com/xml/ns/javaee"
	      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	      xsi:schemaLocation="http://java.sun.com/xml/ns/javaee
	      http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd"
	      version="2.5">
      <xsl:apply-templates select="node()" />
    </web-app>
  </xsl:template>
  
  <xsl:variable name="ns" select=
      "document('')/*/namespace"/>
 
  <xsl:template match="*">
    <xsl:choose>
      <xsl:when test="namespace-uri()=$ns">
        <xsl:element name="{name()}" namespace="{namespace-uri()}">
          <xsl:apply-templates select="@*|node()"/>
        </xsl:element>
      </xsl:when>
      <xsl:otherwise>
        <xsl:element name="{name()}" namespace="{$ns}">
          <xsl:apply-templates select="@*|node()"/>
        </xsl:element>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
 
</xsl:stylesheet>