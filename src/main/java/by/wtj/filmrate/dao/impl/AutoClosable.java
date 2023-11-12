package by.wtj.filmrate.dao.impl;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

public class AutoClosable implements Closeable{
    List<Closeable> objectsToClose;

    void add(Closeable obj){objectsToClose.add(obj);}

    @Override
    public void close() throws IOException {
        for(Closeable o : objectsToClose){
            if(o != null)
                o.close();
        }
    }
}
