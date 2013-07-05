/*
 * Copyright 2011 San Diego Association of Governments (SANDAG)
 * 
 * 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations
 * under the License.
 */
package org.sandag.popsyn.validator;

import org.sandag.popsyn.Version;

/**
 * This class sets the description of labels for the validation statistics output file. The number of validation attributes is data source dependent.
 * 
 */
public class Labels
{
    /**
     * A string array of description of the validation attributes.
     */
    protected String[] labels;
    /**
     * the Version object of this PopSyn II run.
     */
    protected Version  version;

    /**
     * Constructs a newly allocated Labels object with the Version object and sets of the description of the labels.
     * 
     * @param aVersion
     */
    public Labels(Version aVersion)
    {
        version = aVersion;
        init();
    }

    /**
     * Initializes the labels depending on the data source of the observed data.
     */
    private void init()
    {
        if (version.getSourceId() == 1)
        {
            setCensusLabels();
        } else
        {
            setAcsLabels();
        }
    }

    public void setCensusLabels()
    {
        labels = new String[version.getValCount()];
        labels[0] = "Universe 1:  number of households";
        labels[1] = "% family";
        labels[2] = "% non family";
        labels[3] = "% with householder age 15-64";
        labels[4] = "% with householders age 65+";
        labels[5] = "% size 1";
        labels[6] = "% size 2";
        labels[7] = "% size 3";
        labels[8] = "% size 4";
        labels[9] = "% family with householder age 15-64";
        labels[10] = "% family with householder age 65+";
        labels[11] = "% nonfamily with householder age 15-64";
        labels[12] = "% nonfamily with householder age 65+";
        labels[13] = "% family with own children age 0-17";
        labels[14] = "% family without own children age 0-17";
        labels[15] = "% family with 1+ persons age 0-17";
        labels[16] = "% family with 0 persons age 0-17";
        labels[17] = "% nonfamily with 1+ persons age 0-17";
        labels[18] = "% nonfamily with 0 persons age 0-17";
        labels[19] = "% with 1+ persons age 65+";
        labels[20] = "% with 0 persons age 65+";
        labels[21] = "% 1 person HH age 65+";
        labels[22] = "% 2+ person family with 1+ age 65+";
        labels[23] = "% 2+ person nonfamily with 1+ age 65+";
        labels[24] = "% 1 person HH age under 65";
        labels[25] = "% 2+ person family with 0 age 65+";
        labels[26] = "% 2+ person nonfamily with 0 age 65+";
        labels[27] = "% with 0 employed (PT or FT)";
        labels[28] = "% with 1 employed (PT or FT)";
        labels[29] = "% with 2 employed (PT or FT)";
        labels[30] = "% with 3+ employed (PT or FT)";
        labels[31] = "% with income under $30K";
        labels[32] = "% with income $30K-60K";
        labels[33] = "% with income $60K-$100K";
        labels[34] = "% with income $100K-$150K";
        labels[35] = "% with income $150K+";
        labels[36] = "% with income under $10K";
        labels[37] = "% with income $10K-$20K";
        labels[38] = "% with income $20K-$30K";
        labels[39] = "% with income $30K-$40K";
        labels[40] = "% with income $40K-$50K";
        labels[41] = "% with income $50K-$60K";
        labels[42] = "% with income $60K-$75K";
        labels[43] = "% with income $75K-$100K";
        labels[44] = "% with income under $60K";
        labels[45] = "% living in detached single unit house";
        labels[46] = "% living in multi-unit building";
        labels[47] = "% living in mobile home or other";
        labels[48] = "% own or buying dwelling";
        labels[49] = "% renting or occupying without rent";
        labels[50] = " % hh size 1 and 0 worker";
        labels[51] = " % hh size 1 and 1 worker";
        labels[52] = " % hh size 2 and 0 worker";
        labels[53] = " % hh size 2 and 1 worker";
        labels[54] = " % hh size 2 and 2 workers";
        labels[55] = " % hh size 3 and 0 worker";
        labels[56] = " % hh size 3 and 1 worker";
        labels[57] = " % hh size 3 and 2 workers";
        labels[58] = " % hh size 3 and 3 workers";
        labels[59] = " % hh size 4 plus and 0 worker";
        labels[60] = " % hh size 4 plus and 1 worker";
        labels[61] = " % hh size 4 plus and 2 workers";
        labels[62] = " % hh size 4 plus and 3 plus workers";
        labels[63] = " % hh size 1 and hh income under $30k";
        labels[64] = " % hh size 1 and hh income $30k-$60k";
        labels[65] = " % hh size 1 and hh income $60k-$100k";
        labels[66] = " % hh size 1 and hh income $100kplus";
        labels[67] = " % hh size 2 and hh income under $30k";
        labels[68] = " % hh size 2 and hh income $30k-$60k";
        labels[69] = " % hh size 2 and hh income $60k-$100k";
        labels[70] = " % hh size 2 and hh income $100kplus";
        labels[71] = " % hh size 3 and hh income under $30k";
        labels[72] = " % hh size 3 and hh income $30k-$60k";
        labels[73] = " % hh size 3 and hh income $60k-$100k";
        labels[74] = " % hh size 3 and hh income $100kplus";
        labels[75] = " % hh size 4 plus and hh income under $30k";
        labels[76] = " % hh size 4 plus and hh income $30k-$60k";
        labels[77] = " % hh size 4 plus and hh income $60k-$100k";
        labels[78] = " % hh size 4 plus and hh income $100kplus";
        labels[79] = " % hh 0 worker and hh income under $30k";
        labels[80] = " % hh 0 worker and hh income $30k-$60k";
        labels[81] = " % hh 0 worker and hh income $60k-$100k";
        labels[82] = " % hh 0 worker and hh income $100kplus";
        labels[83] = " % hh 1 worker and hh income under $30k";
        labels[84] = " % hh 1 worker and hh income $30k-$60k";
        labels[85] = " % hh 1 worker and hh income $60k-$100k";
        labels[86] = " % hh 1 worker and hh income $100kplus";
        labels[87] = " % hh 2 workers and hh income under $30k";
        labels[88] = " % hh 2 workers and hh income $30k-$60k";
        labels[89] = " % hh 2 workers and hh income $60k-$100k";
        labels[90] = " % hh 2 workers and hh income $100kplus";
        labels[91] = " % hh 3 plus workers and hh income under $30k";
        labels[92] = " % hh 3 plus workers and hh income $30k-$60k";
        labels[93] = " % hh 3 plus workers and hh income $60k-$100k";
        labels[94] = " % hh 3 plus workers and hh income $100kplus";
        labels[95] = "Universe 2:  number of persons in households";
        labels[96] = " % in family households";
        labels[97] = " % in nonfamily households";
        labels[98] = " % with income below poverty level";
        labels[99] = " Universe 3: number of persons in families";
        labels[100] = " % householder";
        labels[101] = " % spouse";
        labels[102] = " % child";
        labels[103] = " % not holder spouse or child";
        labels[104] = " % not related to holder";
        labels[105] = " Universe 4: number of persons (includes GQ)";
        labels[106] = " % male";
        labels[107] = " % female";
        labels[108] = " % age 0-5";
        labels[109] = " % age 6-11";
        labels[110] = " % age 12-15";
        labels[111] = " % age 16-17";
        labels[112] = " % age 18-24";
        labels[113] = " % age 25-34";
        labels[114] = " % age 35-49";
        labels[115] = " % age 50-64";
        labels[116] = " % age 65-79";
        labels[117] = " % age 80+";
        labels[118] = " % age 00-17";
        labels[119] = " % age 18-64";
        labels[120] = " % age 65+";
        labels[121] = " % married with spouse present";
        labels[122] = " % Hispanic or Latino";
        labels[123] = " % White alone";
        labels[124] = " % Black or African American alone";
        labels[125] = " % American Indian or Alaska Native alone";
        labels[126] = " % Asian alone";
        labels[127] = " % Native Hawaiian or Pacific Islander alone";
        labels[128] = " % other race alone or 2+ races";
        labels[129] = " Universe 5: no. persons worked 27+ wks prev. yr";
        labels[130] = " % 35+ hrs/wk";
        labels[131] = " % 15-34 hrs/wk";
        labels[132] = " % 1-14 hrs/wk";
        labels[133] = " Universe 6: number of persons age 16+";
        labels[134] = " % employed (excl armed services)";
        labels[135] = " % in armed forces";
        labels[136] = " % unemployed workers";
        labels[137] = " % not in labor force";
        labels[138] = " % employed by occupation manage/bsness/prof";
        labels[139] = " % employed by occupation service";
        labels[140] = " % employed by occupation sales/office";
        labels[141] = " % employed by occupation construction/maintenance/resource";
        labels[142] = " % employed by occupation production/transport";
        labels[143] = " Universe 7: number of persons age 3+";
        labels[144] = " % enrolled nursery-grade 12";
        labels[145] = " % enrolled post-secondary";
    }

    public void setAcsLabels()
    {
        labels = new String[version.getValCount()];
        labels[0] = "Universe 1:  number of households";
        labels[1] = "% family";
        labels[2] = "% non family";
        labels[3] = "% size 1";
        labels[4] = "% size 2";
        labels[5] = "% size 3";
        labels[6] = "% size 4";
        labels[7] = "% nonfamily with householder age 15-64";
        labels[8] = "% nonfamily with householder age 65+";
        labels[9] = "% family with own children age 0-17";
        labels[10] = "% family without own children age 0-17";
        labels[11] = "% family with 1+ persons age 0-17";
        labels[12] = "% family with 0 persons age 0-17";
        labels[13] = "% nonfamily with 1+ persons age 0-17";
        labels[14] = "% nonfamily with 0 persons age 0-17";
        labels[15] = "% with 1+ persons age 65+";
        labels[16] = "% with 0 persons age 65+";
        labels[17] = "% 1 person HH age 65+";
        labels[18] = "% 2+ person family with 1+ age 65+";
        labels[19] = "% 2+ person nonfamily with 1+ age 65+";
        labels[20] = "% 1 person HH age under 65";
        labels[21] = "% 2+ person family with 0 age 65+";
        labels[22] = "% 2+ person nonfamily with 0 age 65+";
        labels[23] = "% with 0 employed (PT or FT)";
        labels[24] = "% with 1 employed (PT or FT)";
        labels[25] = "% with 2 employed (PT or FT)";
        labels[26] = "% with 3+ employed (PT or FT)";
        labels[27] = "% with income under $30K";
        labels[28] = "% with income $30K-60K";
        labels[29] = "% with income $60K-$100K";
        labels[30] = "% with income $100K-$150K";
        labels[31] = "% with income $150K+";
        labels[32] = "% with income under $10K";
        labels[33] = "% with income $10K-$20K";
        labels[34] = "% with income $20K-$30K";
        labels[35] = "% with income $30K-$40K";
        labels[36] = "% with income $40K-$50K";
        labels[37] = "% with income $50K-$60K";
        labels[38] = "% with income $60K-$75K";
        labels[39] = "% with income $75K-$100K";
        labels[40] = "% with income under $60K";
        labels[41] = "% living in detached single unit house";
        labels[42] = "% living in multi-unit building";
        labels[43] = "% living in mobile home or other";
        labels[44] = "% own or buying dwelling";
        labels[45] = "% renting or occupying without rent";
        labels[46] = " % hh size 1 and 0 worker";
        labels[47] = " % hh size 1 and 1 worker";
        labels[48] = " % hh size 2 and 0 worker";
        labels[49] = " % hh size 2 and 1 worker";
        labels[50] = " % hh size 2 and 2 workers";
        labels[51] = " % hh size 3 and 0 worker";
        labels[52] = " % hh size 3 and 1 worFker";
        labels[53] = " % hh size 3 and 2 workers";
        labels[54] = " % hh size 3 and 3 workers";
        labels[55] = " % hh size 4 plus and 0 worker";
        labels[56] = " % hh size 4 plus and 1 worker";
        labels[57] = " % hh size 4 plus and 2 workers";
        labels[58] = " % hh size 4 plus and 3 plus workers";
        // labels[59]= " % hh size 1 and hh income under $30k";
        // labels[60]= " % hh size 1 and hh income $30k-$60k";
        // labels[61]= " % hh size 1 and hh income $60k-$100k";
        // labels[62]= " % hh size 1 and hh income $100kplus";
        // labels[63]= " % hh size 2 and hh income under $30k";
        // labels[64]= " % hh size 2 and hh income $30k-$60k";
        // labels[65]= " % hh size 2 and hh income $60k-$100k";
        // labels[66]= " % hh size 2 and hh income $100kplus";
        // labels[67]= " % hh size 3 and hh income under $30k";
        // labels[68]= " % hh size 3 and hh income $30k-$60k";
        // labels[69]= " % hh size 3 and hh income $60k-$100k";
        // labels[70]= " % hh size 3 and hh income $100kplus";
        // labels[71]= " % hh size 4 plus and hh income under $30k";
        // labels[72]= " % hh size 4 plus and hh income $30k-$60k";
        // labels[73]= " % hh size 4 plus and hh income $60k-$100k";
        // labels[74]= " % hh size 4 plus and hh income $100kplus";
        // labels[75]= " % hh 0 worker and hh income under $30k";
        // labels[76]= " % hh 0 worker and hh income $30k-$60k";
        // labels[77]= " % hh 0 worker and hh income $60k-$100k";
        // labels[78]= " % hh 0 worker and hh income $100kplus";
        // labels[79]= " % hh 1 worker and hh income under $30k";
        // labels[80]= " % hh 1 worker and hh income $30k-$60k";
        // labels[81]= " % hh 1 worker and hh income $60k-$100k";
        // labels[82]= " % hh 1 worker and hh income $100kplus";
        // labels[83]= " % hh 2 workers and hh income under $30k";
        // labels[84]= " % hh 2 workers and hh income $30k-$60k";
        // labels[85]= " % hh 2 workers and hh income $60k-$100k";
        // labels[86]= " % hh 2 workers and hh income $100kplus";
        // labels[87]= " % hh 3 plus workers and hh income under $30k";
        // labels[88]= " % hh 3 plus workers and hh income $30k-$60k";
        // labels[89]= " % hh 3 plus workers and hh income $60k-$100k";
        // labels[90]= " % hh 3 plus workers and hh income $100kplus";
        labels[59] = "Universe 2: number of persons in households";
        labels[60] = "% in family households";
        labels[61] = "% in nonfamily households";
        labels[62] = "% with income below poverty level";
        labels[63] = "Universe 3: number of persons in families";
        labels[64] = "% householder";
        labels[65] = "% spouse";
        labels[66] = "% child";
        labels[67] = "% not holder spouse or child";
        labels[68] = "% not related to holder";
        labels[69] = "Universe 4: number of persons (includes GQ)";
        labels[70] = "% male";
        labels[71] = "% female";
        labels[72] = "% age 18-24";
        labels[73] = "% age 25-34";
        labels[74] = "% age 35-49";
        labels[75] = "% age 50-64";
        labels[76] = "% age 65-79";
        labels[77] = "% age 80+";
        labels[78] = "% age 00-17";
        labels[79] = "% age 18-64";
        labels[80] = "% age 65+";
        labels[81] = "% Hispanic or Latino";
        labels[82] = "% White alone";
        labels[83] = "% Black or African American alone";
        labels[84] = "% American Indian or Alaska Native alone";
        labels[85] = "% Asian alone";
        labels[86] = "% Native Hawaiian or Pacific Islander alone";
        labels[87] = "% other race alone or 2+ races";
        labels[88] = "Universe 5: no. persons worked 27+ wks prev. yr for pop age 16 to 64";
        labels[89] = "% 35+ hrs/wk";
        labels[90] = "% 15-34 hrs/wk";
        labels[91] = "% 1-14 hrs/wk";
        labels[92] = "Universe 6: number of persons age 16+";
        labels[93] = "% employed (excl armed services)";
        labels[94] = "% in armed forces";
        labels[95] = "% unemployed workers";
        labels[96] = "% not in labor force";
        labels[97] = "% employed by occupation manage/bsness/prof";
        labels[98] = "% employed by occupation service";
        labels[99] = "% employed by occupation sales/office";
        labels[100] = "% employed by occupation natural resources/construction/maintenance/resource";
        labels[101] = "% employed by occupation production/transport";
        labels[102] = "Universe 7: number of persons age 3+";
        labels[103] = "% enrolled nursery-grade 12";
        labels[104] = "% enrolled post-secondary";

    }

    /**
     * Returns a String array of labels.
     * 
     * @return a String array of labels
     */
    public String[] getLabels()
    {
        return labels;
    }

    /**
     * Sets the values of the labels.
     * 
     * @param labels
     *            a String array of labels
     */
    public void setLabels(String[] labels)
    {
        this.labels = labels;
    }
}
