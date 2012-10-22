/*   Copyright (c) 2002-2004 Universidade Federal de Campina Grande
 *
 *   Permission is hereby granted, free of charge, to any person
 *   obtaining a copy of this software and associated documentation
 *   files (the "Software"), to deal with the Software without
 *   restriction, including no limitations in the rights to use, copy,
 *   modify, merge, publish, distribute, sublicense, and/or sell copies
 *   of the Software, and to permit persons to whom the Software is
 *   furnished to do so, subject to the following conditions: The above
 *   copyright notice and this permission notice shall be included in
 *   all copies or substantial portions of the Software.  THE SOFTWARE
 *   IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *   MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *   NONINFRINGEMENT.  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 *   HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITIES,
 *   WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 *   DEALINGS IN THE SOFTWARE.
 *
 *   IMPORTANT:
 *
 *   There are some packages that are distributed with MyGrid:
 *   activation.jar, xmlParserAPIs.jar, xercesImpl.jar, ogsa.jar,
 *   cog-jglobus.jar, mjs.jar and AbsoluteLayout.jar.  Their license
 *   terms are included in Broker documentation, read them before
 *   changing and/or distributing any content of these packages.
*/
import java.io.FileOutputStream;
import java.io.IOException;

/** This class is a sample Bag-of-CPU-Bound-Tasks Applications. 
 */
public class Fat {
   
    public static void main(String args[]) {
        
        long min = Long.parseLong( args[0] );
        long max = Long.parseLong( args[1] );
        long number = Long.parseLong( args[2] );
        String file = args[3];
        
        if( min % 2 == 0)
            min++;
        
        boolean primo = true;
        for( ; min < max; min+=2  ){
            if( number % min == 0 ){
                primo = false;
                break;
            }
        }
        try{
            FileOutputStream fos = new FileOutputStream( file, true );
            if (! primo) {
                fos.write( (  min +"\n").getBytes() );
            }
            fos.close();
        }
        catch( IOException fe ){
            System.err.print( "Error can't write on file" );
        }
    }
}
