package at.fhv.se.banking.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.BiConsumer;

public class TestingUtils {
    
    public static <A, B> void assertEqualsCollections(Collection<A> expected, Collection<B> actual, BiConsumer<A,B> comp) {
        assertEquals(expected.size(), actual.size());
        
        Iterator<A> iterA = expected.iterator();
        Iterator<B> iterB = actual.iterator();

        while(iterA.hasNext() && iterB.hasNext()) {
            comp.accept(iterA.next(), iterB.next());
        }
    }
}