/**
 * View/Controller event support.
 * <h2>Event Activity Types</h2>
 * <table border="1" cellspacing="0">
 * <thead>
 * <tr>
 * <th rowspan="2">{@linkplain scheduler.view.event.ActivityType}</th>
 * <th rowspan="2">Constant Field Name</th>
 * <th colspan="6">Event Name</th>
 * </tr>
 * <tr>
 * <th>{@linkplain scheduler.model.Appointment}</th>
 * <th>{@linkplain scheduler.model.Customer}</th>
 * <th>{@linkplain scheduler.model.Address}</th>
 * <th>{@linkplain scheduler.model.City}</th>
 * <th>{@linkplain scheduler.model.Country}</th>
 * <th>{@linkplain scheduler.model.User}</th>
 * </tr>
 * </thead>
 * <tbody>
 * <tr>
 * <th align="right">{@linkplain scheduler.view.event.ActivityType#EDIT_REQUEST EDIT_REQUEST}</th>
 * <td>EDIT_REQUEST_EVENT_TYPE</td>
 * <td>{@linkplain scheduler.view.event.AppointmentEvent#EDIT_REQUEST_EVENT_TYPE APPOINTMENT_EDIT_REQUEST_EVENT}</td>
 * <td>{@linkplain scheduler.view.event.CustomerEvent#EDIT_REQUEST_EVENT_TYPE CUSTOMER_EDIT_REQUEST_EVENT}</td>
 * <td>{@linkplain scheduler.view.event.AddressEvent#EDIT_REQUEST_EVENT_TYPE ADDRESS_EDIT_REQUEST_EVENT}</td>
 * <td>{@linkplain scheduler.view.event.CityEvent#EDIT_REQUEST_EVENT_TYPE CITY_EDIT_REQUEST_EVENT}</td>
 * <td>{@linkplain scheduler.view.event.CountryEvent#EDIT_REQUEST_EVENT_TYPE COUNTRY_EDIT_REQUEST_EVENT}</td>
 * <td>{@linkplain scheduler.view.event.UserEvent#EDIT_REQUEST_EVENT_TYPE USER_EDIT_REQUEST_EVENT}</td>
 * </tr>
 * <tr>
 * <th align="right">{@linkplain scheduler.view.event.ActivityType#DELETE_REQUEST DELETE_REQUEST}</th>
 * <td>DELETE_REQUEST_EVENT_TYPE</td>
 * <td>{@linkplain scheduler.view.event.AppointmentEvent#DELETE_REQUEST_EVENT_TYPE APPOINTMENT_DELETE_REQUEST_EVENT}</td>
 * <td>{@linkplain scheduler.view.event.CustomerEvent#DELETE_REQUEST_EVENT_TYPE CUSTOMER_DELETE_REQUEST_EVENT}</td>
 * <td>{@linkplain scheduler.view.event.AddressEvent#DELETE_REQUEST_EVENT_TYPE ADDRESS_DELETE_REQUEST_EVENT}</td>
 * <td>{@linkplain scheduler.view.event.CityEvent#DELETE_REQUEST_EVENT_TYPE CITY_DELETE_REQUEST_EVENT}</td>
 * <td>{@linkplain scheduler.view.event.CountryEvent#DELETE_REQUEST_EVENT_TYPE COUNTRY_DELETE_REQUEST_EVENT}</td>
 * <td>{@linkplain scheduler.view.event.UserEvent#DELETE_REQUEST_EVENT_TYPE USER_DELETE_REQUEST_EVENT}</td>
 * </tr>
 * <tr>
 * <th align="right">{@linkplain scheduler.view.event.ActivityType#INSERTING INSERTING}</th>
 * <td>INSERTING_EVENT_TYPE</td>
 * <td>{@linkplain scheduler.view.event.AppointmentEvent#INSERTING_EVENT_TYPE APPOINTMENT_INSERTING_EVENT}</td>
 * <td>{@linkplain scheduler.view.event.CustomerEvent#INSERTING_EVENT_TYPE CUSTOMER_INSERTING_EVENT}</td>
 * <td>{@linkplain scheduler.view.event.AddressEvent#INSERTING_EVENT_TYPE ADDRESS_INSERTING_EVENT}</td>
 * <td>{@linkplain scheduler.view.event.CityEvent#INSERTING_EVENT_TYPE CITY_INSERTING_EVENT}</td>
 * <td>{@linkplain scheduler.view.event.CountryEvent#INSERTING_EVENT_TYPE COUNTRY_INSERTING_EVENT}</td>
 * <td>{@linkplain scheduler.view.event.UserEvent#INSERTING_EVENT_TYPE USER_INSERTING_EVENT}</td>
 * </tr>
 * <tr>
 * <th align="right">{@linkplain scheduler.view.event.ActivityType#INSERTED INSERTED}</th>
 * <td>INSERTED_EVENT_TYPE</td>
 * <td>{@linkplain scheduler.view.event.AppointmentEvent#INSERTED_EVENT_TYPE APPOINTMENT_INSERTED_EVENT}</td>
 * <td>{@linkplain scheduler.view.event.CustomerEvent#INSERTED_EVENT_TYPE CUSTOMER_INSERTED_EVENT}</td>
 * <td>{@linkplain scheduler.view.event.AddressEvent#INSERTED_EVENT_TYPE ADDRESS_INSERTED_EVENT}</td>
 * <td>{@linkplain scheduler.view.event.CityEvent#INSERTED_EVENT_TYPE CITY_INSERTED_EVENT}</td>
 * <td>{@linkplain scheduler.view.event.CountryEvent#INSERTED_EVENT_TYPE COUNTRY_INSERTED_EVENT}</td>
 * <td>{@linkplain scheduler.view.event.UserEvent#INSERTED_EVENT_TYPE USER_INSERTED_EVENT}</td>
 * </tr>
 * <tr>
 * <th align="right">{@linkplain scheduler.view.event.ActivityType#UPDATING UPDATING}</th>
 * <td>UPDATING_EVENT_TYPE</td>
 * <td>{@linkplain scheduler.view.event.AppointmentEvent#UPDATING_EVENT_TYPE APPOINTMENT_UPDATING_EVENT}</td>
 * <td>{@linkplain scheduler.view.event.CustomerEvent#UPDATING_EVENT_TYPE CUSTOMER_UPDATING_EVENT}</td>
 * <td>{@linkplain scheduler.view.event.AddressEvent#UPDATING_EVENT_TYPE ADDRESS_UPDATING_EVENT}</td>
 * <td>{@linkplain scheduler.view.event.CityEvent#UPDATING_EVENT_TYPE CITY_UPDATING_EVENT}</td>
 * <td>{@linkplain scheduler.view.event.CountryEvent#UPDATING_EVENT_TYPE COUNTRY_UPDATING_EVENT}</td>
 * <td>{@linkplain scheduler.view.event.UserEvent#UPDATING_EVENT_TYPE USER_UPDATING_EVENT}</td>
 * </tr>
 * <tr>
 * <th align="right">{@linkplain scheduler.view.event.ActivityType#UPDATED UPDATED}</th>
 * <td>UPDATED_EVENT_TYPE</td>
 * <td>{@linkplain scheduler.view.event.AppointmentEvent#UPDATED_EVENT_TYPE APPOINTMENT_UPDATED_EVENT}</td>
 * <td>{@linkplain scheduler.view.event.CustomerEvent#UPDATED_EVENT_TYPE CUSTOMER_UPDATED_EVENT}</td>
 * <td>{@linkplain scheduler.view.event.AddressEvent#UPDATED_EVENT_TYPE ADDRESS_UPDATED_EVENT}</td>
 * <td>{@linkplain scheduler.view.event.CityEvent#UPDATED_EVENT_TYPE CITY_UPDATED_EVENT}</td>
 * <td>{@linkplain scheduler.view.event.CountryEvent#UPDATED_EVENT_TYPE COUNTRY_UPDATED_EVENT}</td>
 * <td>{@linkplain scheduler.view.event.UserEvent#UPDATED_EVENT_TYPE USER_UPDATED_EVENT}</td>
 * </tr>
 * <tr>
 * <th align="right">{@linkplain scheduler.view.event.ActivityType#DELETING DELETING}</th>
 * <td>DELETING_EVENT_TYPE</td>
 * <td>{@linkplain scheduler.view.event.AppointmentEvent#DELETING_EVENT_TYPE APPOINTMENT_DELETING_EVENT}</td>
 * <td>{@linkplain scheduler.view.event.CustomerEvent#DELETING_EVENT_TYPE CUSTOMER_DELETING_EVENT}</td>
 * <td>{@linkplain scheduler.view.event.AddressEvent#DELETING_EVENT_TYPE ADDRESS_DELETING_EVENT}</td>
 * <td>{@linkplain scheduler.view.event.CityEvent#DELETING_EVENT_TYPE CITY_DELETING_EVENT}</td>
 * <td>{@linkplain scheduler.view.event.CountryEvent#DELETING_EVENT_TYPE COUNTRY_DELETING_EVENT}</td>
 * <td>{@linkplain scheduler.view.event.UserEvent#DELETING_EVENT_TYPE USER_DELETING_EVENT}</td>
 * </tr>
 * <tr>
 * <th align="right">{@linkplain scheduler.view.event.ActivityType#DELETED DELETED}</th>
 * <td>DELETED_EVENT_TYPE</td>
 * <td>{@linkplain scheduler.view.event.AppointmentEvent#DELETED_EVENT_TYPE APPOINTMENT_DELETED_EVENT}</td>
 * <td>{@linkplain scheduler.view.event.CustomerEvent#DELETED_EVENT_TYPE CUSTOMER_DELETED_EVENT}</td>
 * <td>{@linkplain scheduler.view.event.AddressEvent#DELETED_EVENT_TYPE ADDRESS_DELETED_EVENT}</td>
 * <td>{@linkplain scheduler.view.event.CityEvent#DELETED_EVENT_TYPE CITY_DELETED_EVENT}</td>
 * <td>{@linkplain scheduler.view.event.CountryEvent#DELETED_EVENT_TYPE COUNTRY_DELETED_EVENT}</td>
 * <td>{@linkplain scheduler.view.event.UserEvent#DELETED_EVENT_TYPE USER_DELETED_EVENT}</td>
 * </tr>
 * </tbody>
 * </table>
 * <h2>Event Routing</h2>
 * <h3>EDIT_REQUEST</h3>
 * <ul style="list-style-type: none;">
 * <li>{@linkplain scheduler.view.event.AppointmentEvent} &#123;  {@linkplain javafx.event.Event#eventType eventType} = {@linkplain scheduler.view.event.AppointmentEvent#EDIT_REQUEST_EVENT_TYPE};
 *         {@linkplain scheduler.view.event.ModelItemEvent#activity activity} = {@linkplain scheduler.view.event.ActivityType#EDIT_REQUEST EDIT_REQUEST}
 * &#125;</li>
 * <li>{@linkplain scheduler.view.event.CustomerEvent} &#123;  {@linkplain javafx.event.Event#eventType eventType} = {@linkplain scheduler.view.event.CustomerEvent#EDIT_REQUEST_EVENT_TYPE};
 *         {@linkplain scheduler.view.event.ModelItemEvent#activity activity} = {@linkplain scheduler.view.event.ActivityType#EDIT_REQUEST EDIT_REQUEST}
 * &#125;</li>
 * <li>{@linkplain scheduler.view.event.AddressEvent} &#123;  {@linkplain javafx.event.Event#eventType eventType} = {@linkplain scheduler.view.event.AddressEvent#EDIT_REQUEST_EVENT_TYPE};
 *         {@linkplain scheduler.view.event.ModelItemEvent#activity activity} = {@linkplain scheduler.view.event.ActivityType#EDIT_REQUEST EDIT_REQUEST}
 * &#125;</li>
 * <li>{@linkplain scheduler.view.event.CityEvent} &#123;  {@linkplain javafx.event.Event#eventType eventType} = {@linkplain scheduler.view.event.CityEvent#EDIT_REQUEST_EVENT_TYPE};
 *         {@linkplain scheduler.view.event.ModelItemEvent#activity activity} = {@linkplain scheduler.view.event.ActivityType#EDIT_REQUEST EDIT_REQUEST}
 * &#125;</li>
 * <li>{@linkplain scheduler.view.event.CountryEvent} &#123;  {@linkplain javafx.event.Event#eventType eventType} = {@linkplain scheduler.view.event.CountryEvent#EDIT_REQUEST_EVENT_TYPE};
 *         {@linkplain scheduler.view.event.ModelItemEvent#activity activity} = {@linkplain scheduler.view.event.ActivityType#EDIT_REQUEST EDIT_REQUEST}
 * &#125;</li>
 * <li>{@linkplain scheduler.view.event.UserEvent} &#123;  {@linkplain javafx.event.Event#eventType eventType} = {@linkplain scheduler.view.event.UserEvent#EDIT_REQUEST_EVENT_TYPE};
 *         {@linkplain scheduler.view.event.ModelItemEvent#activity activity} = {@linkplain scheduler.view.event.ActivityType#EDIT_REQUEST EDIT_REQUEST}
 * &#125;</li>
 * </ul>
 * <ol>
 * <li>Event Fired
 * <ul>
 * <li> {@linkplain scheduler.fx.ItemEditTableCell} &#x21DC;
 * {@linkplain scheduler.fx.ItemEditTableCell#onEditButtonAction(javafx.event.ActionEvent) ItemEditTableCell.onEditButtonAction(ActionEvent)}
 * <ul style="list-style-type: none;">
 * <li> {@linkplain scheduler.fx.ItemEditTableCellFactory} &#x21DC;
 * {@linkplain scheduler.fx.ItemEditTableCellFactory#onItemActionRequest(scheduler.view.event.ModelItemEvent) ItemEditTableCellFactory.onItemActionRequest(ModelItemEvent)}
 * &#x21A9;
 * <ul style="list-style-type: none;">
 * <li>{@linkplain scheduler.fx.MainListingControl#onItemActionRequest(scheduler.view.event.ModelItemEvent) MainListingControl.onItemActionRequest(ModelItemEvent)}
 * &#x21A9;
 * <ul style="list-style-type: none;">
 * <li>&rArr;
 * {@linkplain scheduler.view.appointment.EditAppointment#edit(scheduler.model.ui.AppointmentModel, javafx.stage.Window) EditAppointment.edit(AppointmentModel, Window)}</li>
 * <li>&rArr;
 * {@linkplain scheduler.view.country.EditCountry#edit(scheduler.model.ui.CountryModel, javafx.stage.Window) EditCountry.edit(CountryModel, Window)}</li>
 * <li>&rArr;
 * {@linkplain scheduler.view.customer.EditCustomer#edit(scheduler.model.ui.CustomerModel, javafx.stage.Window) EditCustomer.edit(CustomerModel, Window)}</li>
 * <li>&rArr; {@linkplain scheduler.view.user.EditUser#edit(scheduler.model.ui.UserModel, javafx.stage.Window) EditUser.edit(UserModel, Window)}</li>
 * </ul>
 * </li>
 * </ul>
 * </li>
 * </ul>
 * </li>
 * <li>{@linkplain scheduler.fx.MainListingControl#onEditMenuItemAction(javafx.event.ActionEvent)}</li>
 * <li>{@linkplain scheduler.fx.MainListingControl#onListingTableViewKeyReleased(javafx.scene.input.KeyEvent)}</li>
 * <li>{@linkplain scheduler.view.address.EditAddress#onCustomerEditMenuItemAction(javafx.event.ActionEvent)}</li>
 * <li>{@linkplain scheduler.view.address.EditAddress#onCustomersTableViewKeyReleased(javafx.scene.input.KeyEvent)}</li>
 * <li>{@linkplain scheduler.view.city.EditCity#onAddressEditMenuItemAction(javafx.event.ActionEvent)}</li>
 * <li>{@linkplain scheduler.view.city.EditCity#onAddressesTableViewKeyReleased(javafx.scene.input.KeyEvent)}</li>
 * <li>{@linkplain scheduler.view.country.EditCountry#onCityEditMenuItemAction(javafx.event.ActionEvent)}</li>
 * <li>{@linkplain scheduler.view.country.EditCountry#onCitiesTableViewKeyReleased(javafx.scene.input.KeyEvent)}</li>
 * <li>{@linkplain scheduler.view.customer.EditCustomer#onEditAppointmentMenuItemAction(javafx.event.ActionEvent)}</li>
 * <li>{@linkplain scheduler.view.customer.EditCustomer#onAppointmentsTableViewTableViewKeyReleased(javafx.scene.input.KeyEvent)}</li>
 * <li>{@linkplain scheduler.view.user.EditUser#onEditAppointmentMenuItemAction(javafx.event.ActionEvent)}</li>
 * <li>{@linkplain scheduler.view.user.EditUser#onAppointmentsTableViewTableViewKeyReleased(javafx.scene.input.KeyEvent)}</li>
 * </ul>
 * </li>
 * <li>Event Consumed
 * <ul>
 * <li>{@linkplain scheduler.view.customer.EditCustomer#onItemActionRequest(scheduler.view.event.AppointmentEvent)} &#x21A9;
 * <ul style="list-style-type: none;">
 * <li>&rArr; {@linkplain scheduler.view.appointment.EditAppointment#edit(scheduler.model.ui.AppointmentModel, javafx.stage.Window)}</li>
 * </ul>
 * </li>
 * <li>{@linkplain scheduler.view.address.EditAddress#onItemActionRequest(scheduler.view.event.CustomerEvent)} &#x21A9;
 * <ul style="list-style-type: none;">
 * <li>&rArr; {@linkplain scheduler.view.customer.EditCustomer#edit(scheduler.model.ui.CustomerModel, javafx.stage.Window)}</li>
 * </ul>
 * </li>
 * <li>{@linkplain scheduler.view.city.EditCity#onItemActionRequest(scheduler.view.event.AddressEvent)} &#x21A9;
 * <ul style="list-style-type: none;">
 * <li>&rArr; {@linkplain scheduler.view.address.EditAddress#edit(scheduler.model.ui.AddressModel, javafx.stage.Window)}</li>
 * </ul>
 * </li>
 * <li>{@linkplain scheduler.view.country.EditCountry#onItemActionRequest(scheduler.view.event.CityEvent)} &#x21A9;
 * <ul style="list-style-type: none;">
 * <li>&rArr; {@linkplain scheduler.view.city.EditCity#edit(scheduler.model.ui.CityModel, javafx.stage.Window)}</li>
 * </ul>
 * </li>
 * <li>{@linkplain scheduler.view.user.EditUser#onItemActionRequest(scheduler.view.event.AppointmentEvent)} &#x21A9;
 * <ul style="list-style-type: none;">
 * <li>&rArr; {@linkplain scheduler.view.appointment.EditAppointment#edit(scheduler.model.ui.AppointmentModel, javafx.stage.Window)}</li>
 * </ul>
 * </li>
 * </ul>
 * </li>
 * </ol>
 * <h3>{@linkplain scheduler.view.event.ActivityType#UPDATING}</h3>
 * <h3>UPDATING</h3>
 * <ul style="list-style-type: none;">
 * <li>{@linkplain scheduler.view.event.AppointmentEvent} &#123;  {@linkplain javafx.event.Event#eventType eventType} = {@linkplain scheduler.view.event.AppointmentEvent#UPDATING_EVENT_TYPE};
 *         {@linkplain scheduler.view.event.ModelItemEvent#activity activity} = {@linkplain scheduler.view.event.ActivityType#UPDATING UPDATING} &#125;</li>
 * <li>{@linkplain scheduler.view.event.CustomerEvent} &#123;  {@linkplain javafx.event.Event#eventType eventType} = {@linkplain scheduler.view.event.CustomerEvent#UPDATING_EVENT_TYPE};
 *         {@linkplain scheduler.view.event.ModelItemEvent#activity activity} = {@linkplain scheduler.view.event.ActivityType#UPDATING UPDATING} &#125;</li>
 * <li>{@linkplain scheduler.view.event.AddressEvent} &#123;  {@linkplain javafx.event.Event#eventType eventType} = {@linkplain scheduler.view.event.AddressEvent#UPDATING_EVENT_TYPE};
 *         {@linkplain scheduler.view.event.ModelItemEvent#activity activity} = {@linkplain scheduler.view.event.ActivityType#UPDATING UPDATING} &#125;</li>
 * <li>{@linkplain scheduler.view.event.CityEvent} &#123;  {@linkplain javafx.event.Event#eventType eventType} = {@linkplain scheduler.view.event.CityEvent#UPDATING_EVENT_TYPE};
 *         {@linkplain scheduler.view.event.ModelItemEvent#activity activity} = {@linkplain scheduler.view.event.ActivityType#UPDATING UPDATING} &#125;</li>
 * <li>{@linkplain scheduler.view.event.CountryEvent} &#123;  {@linkplain javafx.event.Event#eventType eventType} = {@linkplain scheduler.view.event.CountryEvent#UPDATING_EVENT_TYPE};
 *         {@linkplain scheduler.view.event.ModelItemEvent#activity activity} = {@linkplain scheduler.view.event.ActivityType#UPDATING UPDATING} &#125;</li>
 * <li>{@linkplain scheduler.view.event.UserEvent} &#123;  {@linkplain javafx.event.Event#eventType eventType} = {@linkplain scheduler.view.event.UserEvent#UPDATING_EVENT_TYPE};
 *         {@linkplain scheduler.view.event.ModelItemEvent#activity activity} = {@linkplain scheduler.view.event.ActivityType#UPDATING UPDATING} &#125;</li>
 * </ul>
 * <ol>
 * <li>{@linkplain scheduler.view.EditItem.ModelEditor} &#x21DC; {@linkplain scheduler.view.EditItem#onSaveButtonAction(javafx.event.ActionEvent)}
 * <ul style="list-style-type: none;">
 * <li>&#x21E2; {@linkplain scheduler.view.appointment.EditAppointment}</li>
 * <li>&#x21E2; {@linkplain scheduler.view.customer.EditCustomer}</li>
 * <li>&#x21E2; {@linkplain scheduler.view.address.EditAddress}</li>
 * <li>&#x21E2; {@linkplain scheduler.view.city.EditCity}</li>
 * <li>&#x21E2; {@linkplain scheduler.view.country.EditCountry}</li>
 * <li>&#x21E2; {@linkplain scheduler.view.user.EditUser}</li>
 * </ul>
 * </li>
 * <li>&rArr; {@link scheduler.view.EditItem.SaveTask}
 * <ul style="list-style-type: none;">
 * <li>&rArr; {@link scheduler.dao.DataAccessObject.DaoFactory#save(scheduler.view.event.ModelItemEvent, java.sql.Connection, boolean)}</li>
 * </ul>
 * </li>
 * <li>&#x21DD; {@link scheduler.dao.DataAccessObject}
 * <ul style="list-style-type: none;">
 * <li>&#x21E2; {@linkplain scheduler.dao.AppointmentDAO}</li>
 * <li>&#x21E2; {@linkplain scheduler.dao.CustomerDAO}</li>
 * <li>&#x21E2; {@linkplain scheduler.dao.AddressDAO}</li>
 * <li>&#x21E2; {@linkplain scheduler.dao.CityDAO}</li>
 * <li>&#x21E2; {@linkplain scheduler.dao.CountryDAO}</li>
 * <li>&#x21E2; {@linkplain scheduler.dao.UserDAO}</li>
 * </ul>
 * </li>
 * <li>&#x21DD; {@link scheduler.dao.DataAccessObject.DaoFactory DataAccessObject.DaoFactory}
 * <ul style="list-style-type: none;">
 * <li>&#x21E2; {@linkplain scheduler.dao.AppointmentDAO.FactoryImpl AppointmentDAO.FactoryImpl}</li>
 * <li>&#x21E2; {@linkplain scheduler.dao.CustomerDAO.FactoryImpl CustomerDAO.FactoryImpl}</li>
 * <li>&#x21E2; {@linkplain scheduler.dao.AddressDAO.FactoryImpl AddressDAO.FactoryImpl}</li>
 * <li>&#x21E2; {@linkplain scheduler.dao.CityDAO.FactoryImpl CityDAO.FactoryImpl}</li>
 * <li>&#x21E2; {@linkplain scheduler.dao.CountryDAO.FactoryImpl CountryDAO.FactoryImpl}</li>
 * <li>&#x21E2; {@linkplain scheduler.dao.UserDAO.FactoryImpl UserDAO.FactoryImpl}</li>
 * </ul>
 * </li>
 * <li>&#x21DD; {@link scheduler.model.ui.FxRecordModel.ModelFactory FxRecordModel.ModelFactory}
 * <ul style="list-style-type: none;">
 * <li>&#x21E2; {@linkplain scheduler.model.ui.AppointmentModel.Factory AppointmentModel.Factory}</li>
 * <li>&#x21E2; {@linkplain scheduler.model.ui.CustomerModel.Factory CustomerModel.Factory}</li>
 * <li>&#x21E2; {@linkplain scheduler.model.ui.AddressModel.Factory AddressModel.Factory}</li>
 * <li>&#x21E2; {@linkplain scheduler.model.ui.CityModel.Factory CityModel.Factory}</li>
 * <li>&#x21E2; {@linkplain scheduler.model.ui.CountryModel.Factory CountryModel.Factory}</li>
 * <li>&#x21E2; {@linkplain scheduler.model.ui.UserModel.Factory UserModel.Factory}</li>
 * </ul>
 * </li>
 * <li>{@link scheduler.model.ui.FxRecordModel FxRecordModel} &#x21DC;
 * {@linkplain scheduler.model.ui.FxRecordModel.ModelFactory#handleModelEvent(scheduler.view.event.ModelItemEvent) ModelFactory#handleModelEvent(ModelItemEvent)}
 * &#x21A9;
 * <ul style="list-style-type: none;">
 * <li>&#x21E2; {@linkplain scheduler.model.ui.AppointmentModel AppointmentModel}</li>
 * <li>&#x21E2; {@linkplain scheduler.model.ui.CustomerModel CustomerModel}</li>
 * <li>&#x21E2; {@linkplain scheduler.model.ui.AddressModel AddressModel}</li>
 * <li>&#x21E2; {@linkplain scheduler.model.ui.CityModel CityModel}</li>
 * <li>&#x21E2; {@linkplain scheduler.model.ui.CountryModel CountryModel}</li>
 * <li>&#x21E2; {@linkplain scheduler.model.ui.UserModel UserModel}</li>
 * </ul>
 * </li>
 * </ol>
 * <h3>UPDATED</h3>
 * <ul style="list-style-type: none;">
 * <li>{@linkplain scheduler.view.event.AppointmentEvent} &#123;  {@linkplain javafx.event.Event#eventType eventType} = {@linkplain scheduler.view.event.AppointmentEvent#UPDATED_EVENT_TYPE};
 *         {@linkplain scheduler.view.event.ModelItemEvent#activity activity} = {@linkplain scheduler.view.event.ActivityType#UPDATED UPDATED} &#125;</li>
 * <li>{@linkplain scheduler.view.event.CustomerEvent} &#123;  {@linkplain javafx.event.Event#eventType eventType} = {@linkplain scheduler.view.event.CustomerEvent#UPDATED_EVENT_TYPE};
 *         {@linkplain scheduler.view.event.ModelItemEvent#activity activity} = {@linkplain scheduler.view.event.ActivityType#UPDATED UPDATED} &#125;</li>
 * <li>{@linkplain scheduler.view.event.AddressEvent} &#123;  {@linkplain javafx.event.Event#eventType eventType} = {@linkplain scheduler.view.event.AddressEvent#UPDATED_EVENT_TYPE};
 *         {@linkplain scheduler.view.event.ModelItemEvent#activity activity} = {@linkplain scheduler.view.event.ActivityType#UPDATED UPDATED} &#125;</li>
 * <li>{@linkplain scheduler.view.event.CityEvent} &#123;  {@linkplain javafx.event.Event#eventType eventType} = {@linkplain scheduler.view.event.CityEvent#UPDATED_EVENT_TYPE};
 *         {@linkplain scheduler.view.event.ModelItemEvent#activity activity} = {@linkplain scheduler.view.event.ActivityType#UPDATED UPDATED} &#125;</li>
 * <li>{@linkplain scheduler.view.event.CountryEvent} &#123;  {@linkplain javafx.event.Event#eventType eventType} = {@linkplain scheduler.view.event.CountryEvent#UPDATED_EVENT_TYPE};
 *         {@linkplain scheduler.view.event.ModelItemEvent#activity activity} = {@linkplain scheduler.view.event.ActivityType#UPDATED UPDATED} &#125;</li>
 * <li>{@linkplain scheduler.view.event.UserEvent} &#123;  {@linkplain javafx.event.Event#eventType eventType} = {@linkplain scheduler.view.event.UserEvent#UPDATED_EVENT_TYPE};
 *         {@linkplain scheduler.view.event.ModelItemEvent#activity activity} = {@linkplain scheduler.view.event.ActivityType#UPDATED UPDATED} &#125;</li>
 * </ul>
 * <ol>
 * <li>{@link scheduler.dao.DataAccessObject} &#x21DC;
 * {@link scheduler.dao.DataAccessObject.DaoFactory#save(scheduler.view.event.ModelItemEvent, java.sql.Connection, boolean)}
 * <ul style="list-style-type: none;">
 * <li>&#x21E2; {@linkplain scheduler.dao.AppointmentDAO}</li>
 * <li>&#x21E2; {@linkplain scheduler.dao.CustomerDAO}</li>
 * <li>&#x21E2; {@linkplain scheduler.dao.AddressDAO}</li>
 * <li>&#x21E2; {@linkplain scheduler.dao.CityDAO}</li>
 * <li>&#x21E2; {@linkplain scheduler.dao.CountryDAO}</li>
 * <li>&#x21E2; {@linkplain scheduler.dao.UserDAO}</li>
 * </ul>
 * </li>
 * <li>&#x21DD; {@link scheduler.dao.DataAccessObject.DaoFactory DataAccessObject.DaoFactory}
 * <ul style="list-style-type: none;">
 * <li>&#x21E2; {@linkplain scheduler.dao.AppointmentDAO.FactoryImpl ppointmentDAO.FactoryImpl}</li>
 * <li>&#x21E2; {@linkplain scheduler.dao.CustomerDAO.FactoryImpl CustomerDAO.FactoryImpl}</li>
 * <li>&#x21E2; {@linkplain scheduler.dao.AddressDAO.FactoryImpl AddressDAO.FactoryImpl}</li>
 * <li>&#x21E2; {@linkplain scheduler.dao.CityDAO.FactoryImpl CityDAO.FactoryImpl}</li>
 * <li>&#x21E2; {@linkplain scheduler.dao.CountryDAO.FactoryImpl CountryDAO.FactoryImpl}</li>
 * <li>&#x21E2; {@linkplain scheduler.dao.UserDAO.FactoryImpl UserDAO.FactoryImpl}</li>
 * </ul>
 * </li>
 * <li>&#x21DD; {@link scheduler.model.ui.FxRecordModel.ModelFactory FxRecordModel.ModelFactory}
 * <ul style="list-style-type: none;">
 * <li>&#x21E2; {@linkplain scheduler.model.ui.AppointmentModel.Factory AppointmentModel.Factory}</li>
 * <li>&#x21E2; {@linkplain scheduler.model.ui.CustomerModel.Factory CustomerModel.Factory}</li>
 * <li>&#x21E2; {@linkplain scheduler.model.ui.AddressModel.Factory AddressModel.Factory}</li>
 * <li>&#x21E2; {@linkplain scheduler.model.ui.CityModel.Factory CityModel.Factory}</li>
 * <li>&#x21E2; {@linkplain scheduler.model.ui.CountryModel.Factory CountryModel.Factory}</li>
 * <li>&#x21E2; {@linkplain scheduler.model.ui.UserModel.Factory UserModel.Factory}</li>
 * </ul>
 * </li>
 * <li>
 * <ul>
 * <li>{@link scheduler.model.ui.FxRecordModel FxRecordModel} &#x21DC;
 * {@linkplain scheduler.model.ui.FxRecordModel.ModelFactory#handleModelEvent(scheduler.view.event.ModelItemEvent) ModelFactory#handleModelEvent(ModelItemEvent)}
 * &#x21A9;
 * <ul style="list-style-type: none;">
 * <li>&#x21E2; {@linkplain scheduler.model.ui.AppointmentModel AppointmentModel}</li>
 * <li>&#x21E2; {@linkplain scheduler.model.ui.CustomerModel CustomerModel}</li>
 * <li>&#x21E2; {@linkplain scheduler.model.ui.AddressModel AddressModel}</li>
 * <li>&#x21E2; {@linkplain scheduler.model.ui.CityModel CityModel}</li>
 * <li>&#x21E2; {@linkplain scheduler.model.ui.CountryModel CountryModel}</li>
 * <li>&#x21E2; {@linkplain scheduler.model.ui.UserModel UserModel}</li>
 * </ul>
 * </li>
 * <li>{@linkplain scheduler.view.customer.EditCustomer#onAppointmentUpdated(scheduler.view.event.AppointmentEvent) EditCustomer#onAppointmentUpdated(AppointmentEvent)}
 * &#x21A9;</li>
 * <li>{@linkplain scheduler.view.address.EditAddress#onCustomerUpdated(scheduler.view.event.CustomerEvent) EditAddress#onCustomerUpdated(CustomerEvent)}
 * &#x21A9;</li>
 * <li>{@linkplain scheduler.view.city.EditCity#onAddressUpdated(scheduler.view.event.AddressEvent) EditCity#onAddressUpdated(AddressEvent)}
 * &#x21A9;</li>
 * <li>{@linkplain scheduler.view.country.EditCountry#onCityUpdated(scheduler.view.event.CityEvent) EditCountry#onCityUpdated(CityEvent)}
 * &#x21A9;</li>
 * <li>{@linkplain scheduler.view.user.EditUser#onAppointmentUpdated(scheduler.view.event.AppointmentEvent) EditUser#onAppointmentUpdated(AppointmentEvent)}
 * &#x21A9;</li>
 * </ul>
 * </li>
 * </ol>
 */
package scheduler.view.event;
