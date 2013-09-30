package nl.rutgerkok.forgejarcreator;

import java.util.Enumeration;
import java.util.Iterator;

import com.google.common.collect.Iterators;

public class EnumIterable<T> implements Iterable<T> {
    private Enumeration<T> e;

    private EnumIterable(Enumeration<T> e) {
        this.e = e;
    }

    public static <E> Iterable<E> create(Enumeration<E> e) {
        return new EnumIterable<E>(e);
    }

    public Iterator<T> iterator() {
        return Iterators.forEnumeration(e);
    }

}
