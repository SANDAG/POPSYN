/*   
 * Copyright 2014 Parsons Brinckerhoff

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License
   *
   */

package org.sandag.popsyn.testXmlParse;


import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;

public class TargetsSAXParser extends DefaultHandler{

    private Writer out;

    private Balance balanceObject;
    private Marginal[] controlSetArray;
    
    public TargetsSAXParser(){
    }
    
    
    
    public void parseDocument( String xmlFileName ) {
        
        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {
        
            out = new OutputStreamWriter (System.out, "UTF8");

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();
            
            // create a handler object
            TargetsSAXParserHandler handler = new TargetsSAXParserHandler( out );
            
            //parse the file and also register this class for call backs
            sp.parse( xmlFileName, handler );
            
            
        }
        catch(SAXException se) {
            se.printStackTrace();
        }
        catch(ParserConfigurationException pce) {
            pce.printStackTrace();
        }
        catch (IOException ie) {
            ie.printStackTrace();
        }
        
    }

    
    public void parseConditions( String xmlFileName ) {
        
        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {
        
            out = new OutputStreamWriter (System.err, "UTF8");

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();
            
            // create a handler object
            TargetsSAXParserHandler handler = new TargetsSAXParserHandler( out );
            
            //parse the file and also register this class for call backs
            sp.parse( xmlFileName, handler );
            
            controlSetArray = handler.getControlSetArray();
            balanceObject = handler.getBalanceObject();
            
        }
        catch(SAXException se) {
            se.printStackTrace();
        }
        catch(ParserConfigurationException pce) {
            pce.printStackTrace();
        }
        catch (IOException ie) {
            ie.printStackTrace();
        }
        
    }

    
    public Marginal[] getControlSetArray() {        
        return controlSetArray;
    }
    
    public Balance getBalanceObject() {
        return balanceObject;
    }

    
    public static void main(String[] args){

        TargetsSAXParser spe = new TargetsSAXParser();
        
        String xmlFileName = "config/targets.xml";
        spe.parseDocument( xmlFileName );
    }
    
}
