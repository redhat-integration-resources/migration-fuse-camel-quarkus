<xsl:stylesheet version="2.0" 
xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
xmlns:custom="http://custom/extension/functions" 
exclude-result-prefixes="custom">

<xsl:output method="text" encoding="UTF-8"/>

<xsl:param name="response1"/>
<xsl:param name="response2"/>

<xsl:template match="/">

    <xsl:variable name="input1" select="$response1"/>
    <xsl:variable name="input2" select="$response2"/>

    <xsl:variable name="xml">
        <map>
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
            <map key="subscriptions">
                <map key="period">
                    <string key="start">
                        <xsl:value-of select="$input1//start"/>
                    </string>
                    <string key="end">
                        <xsl:value-of select="$input1//end"/>                       
                    </string>
                </map>
                <array key="packages">
                    <xsl:for-each select="$input1//packages">
                        <map>
                            <string key="id">
                                <xsl:value-of select="id"/>
                            </string>
                            <number key="amount">
                                <xsl:value-of select="amount"/>                       
                            </number>
                        </map>
                    </xsl:for-each>
                </array>
            </map>
        </map>   
    </xsl:variable>

    <!-- XML REPRESENTATION OF JSON -->
    <xsl:value-of select="custom:xml-to-json($xml)"/>

</xsl:template>
</xsl:stylesheet>