package by.wtj.filmrate.dao.impl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AutoCloseableListTest {

    static class AutoCloseableTestObject implements AutoCloseable{
        boolean isClosed = false;
        @Override
        public void close() throws Exception {
            isClosed = true;
        }
    }

    @Test
    void autoCloseableListCloseTest(){
        AutoCloseableTestObject obj = new AutoCloseableTestObject();
        try(AutoCloseableList list = new AutoCloseableList()){
            list.add(obj);
            assertFalse(obj.isClosed);
            throw new Exception();
        }catch(Exception e){
            assertTrue(obj.isClosed);
        }
    }

}