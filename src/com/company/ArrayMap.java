package com.company;

import java.util.*;


class ArrayMap <K, V> extends AbstractMap<K, V>{

    //clasa interna ArrayMapEntry ce implementeaza interfata Map.Entry
    class ArrayMapEntry<K, V> implements Map.Entry<K, V>{
        public K key;
        public V value;

        public ArrayMapEntry (K key, V value)
        {
            this.key = key;
            this.value = value;
        }
        public String toString(){
            return value.toString();
        }
        @Override
        public K getKey()
        {
            return this.key;
        }

        @Override
        public V getValue()
        {
            return this.value;
        }
        //metoda ce seteaza cu o valoare noua valoarea curenta si returneaza valoarea curenta
        @Override
        public V setValue(V value) {
            V old = this.value;
            this.value = value;
            return old;
        }

        public int hashCode ()
        {
            return (this.getKey()==null   ? 0 : this.getKey().hashCode()) ^
                    (this.getValue()==null ? 0 : this.getValue().hashCode());
        }


        //metoda ce verifica daca un obiect este o instanta a clasei ArrayMapEntry si daca este egala cu instanta curenta(this)
        public boolean equals(Object other){
            if (!(other instanceof ArrayMapEntry))
                return false;
            ArrayMapEntry<K, V> o = (ArrayMapEntry<K, V>) other;
            return (this.getKey()==null ?
                    o.getKey()==null : this.getKey().equals(o.getKey())  &&
                    (this.getValue()==null ?
                            o.getValue()==null : this.getValue().equals(o.getValue())));
        }

    }

    public Set<Map.Entry<K, V>> entries = null;
    public ArrayList<ArrayMapEntry<K, V>> list = null;
    public ArrayMap ()
    {
        list = new ArrayList<>();
    }
    public int size ()
    {
        return list.size();
    }

    //Metoda ce returneaza un Set de entr-uri
    public Set<Map.Entry<K, V>> entrySet() {

        if (entries == null) {
            entries = new AbstractSet<>() {
                public void clear() {
                    list.clear();
                }

                @SuppressWarnings("rawtypes")
                public Iterator iterator() {
                    return list.iterator();
                }

                public int size() {
                    return list.size();
                }
            };
        }
        return entries;
    }

    //metoda ce adauga o valoare in lista de entry-uri, adica in ArrayMap
    public V put(K key, V value)
    {
        int size = list.size();
        ArrayMapEntry<K, V> entry = null;
        int i;
        if (key == null) {
            for (i = 0; i < size; i++) {
                entry = list.get(i);
                if (entry.getKey() == null) {
                    break;
                }
            }
        }
        else
        {
            for (i = 0; i < size; i++) {
                entry = list.get(i);
                if (key.equals(entry.getKey())) {
                    break;
                }
            }
        }
        V oldValue = null;
        if (i < size) {
            oldValue = entry.getValue();
            entry.setValue((V) value);
        } else {
            list.add(new ArrayMapEntry(key, value));
        }
        return oldValue;
    }

    @Override
    public String toString() {
        return  list.toString();
    }
    //metoda ce verifica daca exista o cheie in ArrayMap prin verificarea daca exista un ArrayMapEntry cu acea cheie
    public boolean containsKey(Object key)
    {
        K newKey = (K) key;
        for(int i = 0; i < size();i++)
        {
            if(this.list.get(i).getKey() == newKey)
            {
                return true;
            }
        }
        return false;
    }
    //Metoda ce returneaza valoarea din ArrayMapEntry-ul cu cheia K, se cauta in lista si se returneaza daca este gasita
    public V get(Object key){
        if(containsKey(key)){
            K newKey = (K) key;
            for (int i = 0; i < size(); i++) {
                if (this.list.get(i).getKey() == newKey) {
                    return this.list.get(i).getValue();
                }
            }
        }
        return null;
    }
}
