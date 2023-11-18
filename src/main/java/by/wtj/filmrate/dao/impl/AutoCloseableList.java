package by.wtj.filmrate.dao.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AutoCloseableList implements AutoCloseable{
    List<AutoCloseable> objectsToClose;
    public AutoCloseableList(){objectsToClose = new ArrayList<>();}

    void add(AutoCloseable obj){objectsToClose.add(obj);}

    @Override
    public void close() throws IOException {
        for(AutoCloseable o : objectsToClose){
            if(o != null)
                try{
                    o.close();
                }catch(Exception e){
                    throw new IOException(e);
                }
        }
    }
}
