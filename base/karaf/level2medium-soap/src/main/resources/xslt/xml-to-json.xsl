<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="text" encoding="UTF-8"/>

  <xsl:template match="map[not(@key)]">
    {<xsl:apply-templates select="./*"/>}<xsl:if test="position() != last()">,</xsl:if>
  </xsl:template>

  <xsl:template match="map[@key]">
    "<xsl:value-of select="./@key"/>":{<xsl:apply-templates select="./*"/>}<xsl:if test="position() != last()">,</xsl:if>
  </xsl:template>

  <xsl:template match="array[@key]">
    "<xsl:value-of select="./@key"/>":[<xsl:apply-templates select="./*"/>]<xsl:if test="position() != last()">,</xsl:if>
  </xsl:template>

  <xsl:template match="string[@key]">
    "<xsl:value-of select="./@key"/>":"<xsl:value-of select="."/>"<xsl:if test="position() != last()">,</xsl:if>
  </xsl:template>

  <xsl:template match="number[@key]">
    "<xsl:value-of select="./@key"/>":<xsl:value-of select="."/><xsl:if test="position() != last()">,</xsl:if>
  </xsl:template>

  <xsl:template match="number[not(@key)]">
    <xsl:value-of select="."/><xsl:if test="position() != last()">,</xsl:if>
  </xsl:template>

</xsl:stylesheet>