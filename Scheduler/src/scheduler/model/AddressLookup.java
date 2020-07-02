package scheduler.model;

import scheduler.util.Values;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface AddressLookup {

    public static AddressLookup of(String _address1, String _address2, String _postalCode, String _phone) {
        return new AddressLookup() {
            private final String address1 = Values.asNonNullAndWsNormalized(_address1);
            private final String address2 = Values.asNonNullAndWsNormalized(_address2);
            private final String postalCode = Values.asNonNullAndWsNormalized(_postalCode);
            private final String phone = Values.asNonNullAndWsNormalized(_phone);

            @Override
            public String getAddress1() {
                return address1;
            }

            @Override
            public String getAddress2() {
                return address2;
            }

            @Override
            public String getPostalCode() {
                return postalCode;
            }

            @Override
            public String getPhone() {
                return phone;
            }

            @Override
            public AddressLookup asNormalizedAddressLookup() {
                return this;
            }

        };
    }

    default AddressLookup asNormalizedAddressLookup() {
        return new AddressLookup() {
            private final String address1 = Values.asNonNullAndWsNormalized(AddressLookup.this.getAddress1());
            private final String address2 = Values.asNonNullAndWsNormalized(AddressLookup.this.getAddress2());
            private final String postalCode = Values.asNonNullAndWsNormalized(AddressLookup.this.getPostalCode());
            private final String phone = Values.asNonNullAndWsNormalized(AddressLookup.this.getPhone());

            @Override
            public String getAddress1() {
                return address1;
            }

            @Override
            public String getAddress2() {
                return address2;
            }

            @Override
            public String getPostalCode() {
                return postalCode;
            }

            @Override
            public String getPhone() {
                return phone;
            }

            @Override
            public AddressLookup asNormalizedAddressLookup() {
                return this;
            }

        };
    }

    /**
     * Gets the first line of the current address.
     *
     * @return the first line of the current address.
     */
    String getAddress1();

    /**
     * Gets the second line of the current address.
     *
     * @return the second line of the current address.
     */
    String getAddress2();

    /**
     * Gets the postal code for the current address.
     *
     * @return the postal code for the current address.
     */
    String getPostalCode();

    /**
     * Gets the phone number associated with the current address.
     *
     * @return the phone number associated with the current address.
     */
    String getPhone();

}
