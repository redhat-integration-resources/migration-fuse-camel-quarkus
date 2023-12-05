<xsl:stylesheet version="3.0" 
xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
xmlns="http://www.w3.org/2005/xpath-functions">

<xsl:output method="text" encoding="UTF-8"/>

<xsl:param name="response1"/>
<xsl:param name="response2"/>

<xsl:template match="/">

    <!-- MAP XML INPUT TO XML FOR JSON 
         as per: https://www.w3.org/TR/xslt-30/#json-to-xml-mapping -->
        <xsl:variable name="input1" select="json-to-xml($response1)"/>
        <xsl:variable name="input2" select="parse-xml($response2)"/>

    <xsl:variable name="xml">

        <map>
            <!-- Data Mapping from the SOAP response -->
            <map key="client">
                <string key="fullName">
                    <xsl:value-of select="concat($input2//Name,' ',$input2//Surname)"/>
                </string>
                <string key="addressLine1">
                    <xsl:value-of select="concat($input2//Number,' ',$input2//Street)"/>
                </string>
                <string key="addressLine2">
                    <xsl:value-of select="concat($input2//City,' ',$input2//PostCode)"/>
                </string>
                <string key="addressLine3">
                    <xsl:value-of select="$input2//Country"/>
                </string>
            </map>

            <!-- Data Mapping from the REST response (straight copy) -->
            <xsl:copy-of select="$input1/*:map/*:map"/>
        </map>   
    </xsl:variable>

    <!-- JSON OUTPUT -->
    <xsl:value-of select="xml-to-json($xml)"/>

</xsl:template>
</xsl:stylesheet>