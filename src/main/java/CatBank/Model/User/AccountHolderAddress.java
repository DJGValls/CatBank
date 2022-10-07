package CatBank.Model.User;

import javax.persistence.Embeddable;

@Embeddable
public class AccountHolderAddress {

    private String country;

    private String address;

    private String city;

    private int postalCode;

    public AccountHolderAddress() {
    }

    public AccountHolderAddress(String country, String address, String city, int postalCode) {
        this.country = country;
        this.address = address;
        this.city = city;
        this.postalCode = postalCode;
    }

    public AccountHolderAddress(String address) {
        this.address = address;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(int postalCode) {
        this.postalCode = postalCode;
    }
}
