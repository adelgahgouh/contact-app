package com.codetree.contact;

public class Contact {
    public String id,contactname,contacttel;
    public Contact(){

    }
   public Contact(String id,String contactname,String contacttel){
        this.contactname=contactname;
        this.id=id;
        this.contacttel=contacttel;
    }

    public String getId() {
        return id;
    }

    public String getContactname() {
        return contactname;
    }

    public String getContacttel() {
        return contacttel;
    }
}
