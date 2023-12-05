<xsl:stylesheet version="2.0" 
xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
>

	<!-- The actual data mapping -->
	<xsl:template match="/">
      <s1:SubscriberRequest xmlns:s1="http://www.example.org/s1/">
         <Id><xsl:value-of select="//id"/></Id>
      </s1:SubscriberRequest>
	</xsl:template>
			
</xsl:stylesheet>