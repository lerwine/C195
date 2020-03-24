/**
 * Data access objects and supporting classes.
 * 
 * <dl>
 * 	<dt>{@link scheduler.dao.DataObject}</dt>
 * 	<dd>Base interface for all data objects representing a data row in the database.
 * 		<dl>
 * 			<dt>{@link scheduler.dao.DataObjectImpl}</dt>
 * 			<dd>Base class for data objects representing a data row in the database. (concrete implementation of {@link scheduler.dao.DataObject}).
 * 				<dl>
 * 					<dt>{@link scheduler.dao.DataObjectImpl.Factory}</dt>
 * 					<dd>Base class for CRUD operations on {@link scheduler.dao.DataObjectImpl} objects.</dd>
 * 					<dt>{@link scheduler.dao.DataObjectImpl.DataObjectModel}</dt>
 * 					<dd>Base class for FXML models of {@link DataObject} properties.</dd>
 * 					<dt>{@link scheduler.dao.DataObjectImpl.DataObjectReferenceModelImpl}</dt>
 * 					<dd>Base class for FXML models of {@link DataObjectImpl} properties.</dd>
 * 				</dl>
 * 			</dd>
 * 			<dt>{@link scheduler.dao.Appointment}</dt>
 * 			<dd>Interface for data from the "appointment" database table.
 * 				<dl>
 * 					<dt>{@link scheduler.dao.AppointmentImpl}</dt>
 * 					<dd>Concrete implementation</dd>
 * 				</dl>
 * 			</dd>
 * 			<dt>{@link scheduler.dao.Customer}</dt>
 * 			<dd>Interface for data from the "customer" database table. <em>(referenced by {@link scheduler.dao.Appointment#getCustomer()})</em>
 * 				<dl>
 * 					<dt>{@link scheduler.dao.CustomerImpl}</dt>
 * 					<dd>Concrete implementation <em>(referenced by {@link scheduler.dao.AppointmentImpl#getCustomer()})</em></dd>
 * 				</dl>
 * 			</dd>
 * 			<dt>{@link scheduler.dao.Address}</dt>
 * 			<dd>Interface for data from the "address" database table. <em>(referenced by {@link scheduler.dao.Customer#getAddress()})</em>
 * 				<dl>
 * 					<dt>{@link scheduler.dao.AddressImpl}</dt>
 * 					<dd>Concrete implementation <em>(referenced by {@link scheduler.dao.CustomerImpl#getAddress()})</em></dd>
 * 				</dl>
 * 			</dd>
 * 			<dt>{@link scheduler.dao.City}</dt>
 * 			<dd>Interface for data from the "city" database table. <em>(referenced by {@link scheduler.dao.Address#getCity()})</em>
 * 				<dl>
 * 					<dt>{@link scheduler.dao.CityImpl}</dt>
 * 					<dd>Concrete implementation <em>(referenced by {@link scheduler.dao.AddressImpl#getCity()})</em></dd>
 * 				</dl>
 * 			</dd>
 * 			<dt>{@link scheduler.dao.Country}</dt>
 * 			<dd>Interface for data from the "country" database table. <em>(referenced by {@link scheduler.dao.City#getCountry()})</em>
 * 				<dl>
 * 					<dt>{@link scheduler.dao.CountryImpl}</dt>
 * 					<dd>Concrete implementation <em>(referenced by {@link scheduler.dao.CityImpl#getCountry()})</em></dd>
 * 				</dl>
 * 			</dd>
 * 			<dt>{@link scheduler.dao.User}</dt>
 * 			<dd>Data row from the "user" database table. <em>(referenced by {@link scheduler.dao.Appointment#getUser()})</em>
 * 				<dl>
 * 					<dt>{@link scheduler.dao.UserImpl}</dt>
 * 					<dd>Concrete implementation <em>(referenced by {@link scheduler.dao.AppointmentImpl#getUser()})</em></dd>
 * 				</dl>
 * 			</dd>
 * 		</dl>
 * 	</dd>
 * 	<dt>{@link scheduler.dao.RecordReader}</dt>
 * 	<dd>Interface reading {@link DataObjectImpl} objects from the database.
 * 		<dl>
 * 			<dt>{@link scheduler.dao.ModelListingFilter}</dt>
 * 			<dd>A {@link scheduler.dao.RecordReader} that defines a filtered view of {@link scheduler.view.ItemModel} objects.
 * 				<dl>
 * 					<dt>Implementations</dt>
 * 					<dd>{@link scheduler.dao.AppointmentFilter}, {@link scheduler.dao.CustomerFilter}, {@link scheduler.dao.UserFilter}</dd>
 * 				</dl>
 * 			</dd>
 * 		</dl>
 * 	</dd>
 * </dl>
 */
package scheduler.dao;
