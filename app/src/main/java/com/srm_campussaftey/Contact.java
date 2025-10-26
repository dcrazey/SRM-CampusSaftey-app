package com.srm_campussaftey;

/**
 * This is a simple "data class" that just holds information about a single contact.
 * It has two properties: a name and a phone number.
 */
public class Contact {

    private String name;
    private String phone;

    /**
     * Constructor to create a new Contact object.
     *
     * @param name  The contact's name (e.g., "Mom")
     * @param phone The contact's phone number (e.g., "9876543210")
     */
    public Contact(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    // --- Getters ---
    // These are methods that allow other classes to READ the private data.

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    // --- Setters ---
    // These are methods that allow other classes to CHANGE the private data.

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}

