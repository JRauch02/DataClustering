//Jonathan Rauch

//Fpund the example for this code on this website
//https://www.baeldung.com/java-write-console-output-file

package phase4;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

class DualPrintStream extends PrintStream {
    private final PrintStream second;

    public DualPrintStream(OutputStream main, PrintStream second) {
        super(main);
        this.second = second;
    }
    
    @Override
    public void write(byte[] b) throws IOException {
        super.write(b);
        second.write(b);
    }
}
