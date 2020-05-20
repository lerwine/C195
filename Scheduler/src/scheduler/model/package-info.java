    /**
 * Base data model definitions.
 * <table style="border: 1px solid black; border-collapse: collapse" class="constantsSummary" border="1">
 *   <caption>Data access object inheritance table</caption>
 *   <thead>
 *     <tr>
 *       <th colspan="2">Base Model Type</th>
 *       <th colspan="2" style="background-color:#cccccc">Data Access Object Type</th>
 *       <th colspan="2">JavaFX Item Model Type</th>
 *     </tr>
 *     <tr>
 *       <th>Root Type</th>
 *       <th>Record Type</th>
 *       <th style="background-color:#cccccc">Root Type</th>
 *       <th style="background-color:#cccccc">Record Type</th>
 *       <th>Root Type</th>
 *       <th>Record Type</th>
 *     </tr>
 *   </thead>
 *   <tbody>
 *     <tr>
 *         <td style="vertical-align: top;">
 *         <a class="typeNameLink" href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *         </td>
 *         <td style="vertical-align: top;">
 *         <a class="typeNameLink" href="{@docRoot}/scheduler/model/DataRecord.html">DataRecord</a>
 *         <br>Inherits:
 *         <ul class="inheritance">
 *             <li>
 *             <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *             </li>
 *         </ul>
 *         </td>
 *         <td style="vertical-align: top;">
 *         <a class="typeNameLink" href="{@docRoot}/scheduler/daoDbObject.html">DAO</a>
 *         <br>Inherits:
 *         <ul class="inheritance">
 *             <li>
 *             <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *             </li>
 *         </ul>
 *         </td>
 *         <td style="vertical-align: top;">
 *         <a class="typeNameLink" href="{@docRoot}/scheduler/dao/DataAccessObject.html">DataAccessObject</a>
 *         <br>Inherits:
 *         <ul class="inheritance">
 *             <li>
 *             <a href="{@docRoot}/scheduler/dao/DbRecord.html">DbRecord</a>
 *             <ul class="inheritance">
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/daoDbObject.html">DAO</a>
 *                 <ul class="inheritance">
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                     </li>
 *                 </ul>
 *                 </li>
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/model/DataRecord.html">DataRecord</a>
 *                 <ul class="inheritance">
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                     </li>
 *                 </ul>
 *                 </li>
 *             </ul>
 *             </li>
 *         </ul>
 *         </td>
 *         <td style="vertical-align: top;">
 *         <a class="typeNameLink" href="{@docRoot}/scheduler/model/ui/FxModel.html">FxModel</a>
 *         <br>Inherits:
 *         <ul class="inheritance">
 *             <li>
 *             <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *             </li>
 *         </ul>
 *         </td>
 *         <td style="vertical-align: top;">
 *         <a class="typeNameLink" href="{@docRoot}/scheduler/model/ui/FxRecordModel.html">FxRecordModel</a>
 *         <br>Inherits:
 *         <ul class="inheritance">
 *             <li>
 *             <a href="{@docRoot}/scheduler/model/ui/IFxRecordModel.html">IFxRecordModel</a>
 *             <ul class="inheritance">
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/model/ui/FxDbModel.html">FxDbModel</a>
 *                 <ul class="inheritance">
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/model/ui/FxModel.html">FxModel</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                 </ul>
 *                 </li>
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/model/DataRecord.html">DataRecord</a>
 *                 <ul class="inheritance">
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                     </li>
 *                 </ul>
 *                 </li>
 *             </ul>
 *             </li>
 *         </ul>
 *         </td>
 *     </tr>
 *     <tr>
 *         <td style="vertical-align: top;">
 *         <a class="typeNameLink" href="{@docRoot}/scheduler/model/Address.html">Address</a>
 *         <br>Inherits:
 *         <ul class="inheritance">
 *             <li>
 *             <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *             </li>
 *         </ul>
 *         </td>
 *         <td style="vertical-align: top;">
 *         <a class="typeNameLink" href="{@docRoot}/scheduler/model/AddressRecord.html">AddressRecord</a>
 *         <br>Inherits:
 *         <ul class="inheritance">
 *             <li>
 *             <a href="{@docRoot}/scheduler/model/DataRecord.html">DataRecord</a>
 *             <ul class="inheritance">
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                 </li>
 *             </ul>
 *             </li>
 *             <li>
 *             <a href="{@docRoot}/scheduler/model/Address.html">Address</a>
 *             <ul class="inheritance">
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                 </li>
 *             </ul>
 *             </li>
 *         </ul>
 *         </td>
 *         <td style="vertical-align: top;">
 *         <a class="typeNameLink" href="{@docRoot}/scheduler/dao/IAddressDAO.html">IAddressDAO</a>
 *         <br>Inherits:
 *         <ul class="inheritance">
 *             <li>
 *             <a href="{@docRoot}/scheduler/daoDbObject.html">DAO</a>
 *             <ul class="inheritance">
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                 </li>
 *             </ul>
 *             </li>
 *             <li>
 *             <a href="{@docRoot}/scheduler/model/Address.html">Address</a>
 *             <ul class="inheritance">
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                 </li>
 *             </ul>
 *             </li>
 *         </ul>
 *         </td>
 *         <td style="vertical-align: top;">
 *         <a class="typeNameLink" href="{@docRoot}/scheduler/dao/AddressDAO.html">AddressDAO</a>
 *         <br>Inherits:
 *         <ul class="inheritance">
 *             <li>
 *             <a href="{@docRoot}/scheduler/dao/DataAccessObject.html">DataAccessObject</a>
 *             <ul class="inheritance">
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/dao/DbRecord.html">DbRecord</a>
 *                 <ul class="inheritance">
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/daoDbObject.html">DAO</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/model/DataRecord.html">DataRecord</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                 </ul>
 *                 </li>
 *             </ul>
 *             </li>
 *             <li>
 *             <a href="{@docRoot}/scheduler/dao/AddressDbRecord.html">AddressDbRecord</a>
 *             <ul class="inheritance">
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/dao/IAddressDAO.html">IAddressDAO</a>
 *                 <ul class="inheritance">
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/daoDbObject.html">DAO</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/model/Address.html">Address</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                 </ul>
 *                 </li>
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/dao/DbRecord.html">DbRecord</a>
 *                 <ul class="inheritance">
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/daoDbObject.html">DAO</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/model/DataRecord.html">DataRecord</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                 </ul>
 *                 </li>
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/model/AddressRecord.html">AddressRecord</a>
 *                 <ul class="inheritance">
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/model/DataRecord.html">DataRecord</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/model/Address.html">Address</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                 </ul>
 *                 </li>
 *             </ul>
 *             </li>
 *         </ul>
 *         </td>
 *         <td style="vertical-align: top;">
 *         <a class="typeNameLink" href="{@docRoot}/scheduler/model/ui/AddressItem.html">AddressItem</a>
 *         <br>Inherits:
 *         <ul class="inheritance">
 *             <li>
 *             <a href="{@docRoot}/scheduler/model/Address.html">Address</a>
 *             <ul class="inheritance">
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                 </li>
 *             </ul>
 *             </li>
 *             <li>
 *             <a href="{@docRoot}/scheduler/model/ui/FxModel.html">FxModel</a>
 *             <ul class="inheritance">
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                 </li>
 *             </ul>
 *             </li>
 *         </ul>
 *         </td>
 *         <td style="vertical-align: top;">
 *         <a class="typeNameLink" href="{@docRoot}/scheduler/model/ui/AddressModel.html">AddressModel</a>
 *         <br>Inherits:
 *         <ul class="inheritance">
 *             <li>
 *             <a href="{@docRoot}/scheduler/model/ui/FxRecordModel.html">FxRecordModel</a>
 *             <ul class="inheritance">
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/model/ui/IFxRecordModel.html">IFxRecordModel</a>
 *                 <ul class="inheritance">
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/model/ui/FxDbModel.html">FxDbModel</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/ui/FxModel.html">FxModel</a>
 *                         <ul class="inheritance">
 *                             <li>
 *                             <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                             </li>
 *                         </ul>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/model/DataRecord.html">DataRecord</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                 </ul>
 *                 </li>
 *             </ul>
 *             </li>
 *             <li>
 *             <a href="{@docRoot}/scheduler/model/ui/AddressDbItem.html">AddressDbItem</a>
 *             <ul class="inheritance">
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/model/ui/AddressItem.html">AddressItem</a>
 *                 <ul class="inheritance">
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/model/Address.html">Address</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/model/ui/FxModel.html">FxModel</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                 </ul>
 *                 </li>
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/model/ui/FxDbModel.html">FxDbModel</a>
 *                 <ul class="inheritance">
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/model/ui/FxModel.html">FxModel</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                 </ul>
 *                 </li>
 *             </ul>
 *             </li>
 *         </ul>
 *         </td>
 *     </tr>
 * 
 *     <tr>
 *         <td style="vertical-align: top;">
 *         <a class="typeNameLink" href="{@docRoot}/scheduler/model/Appointment.html">Appointment</a>
 *         <ul class="inheritance">
 *             <li>
 *             <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *             </li>
 *         </ul>
 *         </td>
 *         <td style="vertical-align: top;">
 *         <a class="typeNameLink" href="{@docRoot}/scheduler/model/AppointmentRecord.html">AppointmentRecord</a>
 *         <ul class="inheritance">
 *             <li>
 *             <a href="{@docRoot}/scheduler/model/DataRecord.html">DataRecord</a>
 *             <ul class="inheritance">
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                 </li>
 *             </ul>
 *             </li>
 *             <li>
 *             <a href="{@docRoot}/scheduler/model/Appointment.html">Appointment</a>
 *             <ul class="inheritance">
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                 </li>
 *             </ul>
 *             </li>
 *         </ul>
 *         </td>
 *         <td style="vertical-align: top; background-color:#cccccc;">
 *         <a class="typeNameLink" href="{@docRoot}/scheduler/dao/IAppointmentDAO.html">IAppointmentDAO</a>
 *         <ul class="inheritance">
 *             <li>
 *             <a href="{@docRoot}/scheduler/daoDbObject.html">DAO</a>
 *             <ul class="inheritance">
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                 </li>
 *             </ul>
 *             </li>
 *             <li>
 *             <a href="{@docRoot}/scheduler/model/Appointment.html">Appointment</a>
 *             <ul class="inheritance">
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                 </li>
 *             </ul>
 *             </li>
 *         </ul>
 *         </td>
 *         <td style="vertical-align: top; background-color:#cccccc;">
 *         <a class="typeNameLink" href="{@docRoot}/scheduler/dao/AppointmentDAO.html">AppointmentDAO</a>
 *         <ul class="inheritance">
 *             <li>
 *             <a href="{@docRoot}/scheduler/dao/DataAccessObject.html">DataAccessObject</a>
 *             <ul class="inheritance">
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/dao/DbRecord.html">DbRecord</a>
 *                 <ul class="inheritance">
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/daoDbObject.html">DAO</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/model/DataRecord.html">DataRecord</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                 </ul>
 *                 </li>
 *             </ul>
 *             </li>
 *             <li>
 *             <a href="{@docRoot}/scheduler/dao/AppointmentDbRecord.html">AppointmentDbRecord</a>
 *             <ul class="inheritance">
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/dao/IAppointmentDAO.html">IAppointmentDAO</a>
 *                 <ul class="inheritance">
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/daoDbObject.html">DAO</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/model/Appointment.html">Appointment</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                 </ul>
 *                 </li>
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/dao/DbRecord.html">DbRecord</a>
 *                 <ul class="inheritance">
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/daoDbObject.html">DAO</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/model/DataRecord.html">DataRecord</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                 </ul>
 *                 </li>
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/model/AppointmentRecord.html">AppointmentRecord</a>
 *                 <ul class="inheritance">
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/model/DataRecord.html">DataRecord</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/model/Appointment.html">Appointment</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                 </ul>
 *                 </li>
 *             </ul>
 *             </li>
 *         </ul>
 *         </td>
 *         <td style="vertical-align: top;">
 *         <a class="typeNameLink" href="{@docRoot}/scheduler/model/ui/AppointmentItem.html">AppointmentItem</a>
 *         <ul class="inheritance">
 *             <li>
 *             <a href="{@docRoot}/scheduler/model/Appointment.html">Appointment</a>
 *             <ul class="inheritance">
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                 </li>
 *             </ul>
 *             </li>
 *             <li>
 *             <a href="{@docRoot}/scheduler/model/ui/FxModel.html">FxModel</a>
 *             <ul class="inheritance">
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                 </li>
 *             </ul>
 *             </li>
 *         </ul>
 *         </td>
 *         <td style="vertical-align: top;">
 *         <a class="typeNameLink" href="{@docRoot}/scheduler/model/ui/AppointmentModel.html">AppointmentModel</a>
 *         <ul class="inheritance">
 *             <li>
 *             <a href="{@docRoot}/scheduler/model/ui/FxRecordModel.html">FxRecordModel</a>
 *             <ul class="inheritance">
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/model/ui/IFxRecordModel.html">IFxRecordModel</a>
 *                 <ul class="inheritance">
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/model/ui/FxDbModel.html">FxDbModel</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/ui/FxModel.html">FxModel</a>
 *                         <ul class="inheritance">
 *                             <li>
 *                             <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                             </li>
 *                         </ul>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/model/DataRecord.html">DataRecord</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                 </ul>
 *                 </li>
 *             </ul>
 *             </li>
 *             <li>
 *             <a href="{@docRoot}/scheduler/model/ui/AppointmentDbItem.html">AppointmentDbItem</a>
 *             <ul class="inheritance">
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/model/ui/AppointmentItem.html">AppointmentItem</a>
 *                 <ul class="inheritance">
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/model/Appointment.html">Appointment</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/model/ui/FxModel.html">FxModel</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                 </ul>
 *                 </li>
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/model/ui/FxDbModel.html">FxDbModel</a>
 *                 <ul class="inheritance">
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/model/ui/FxModel.html">FxModel</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                 </ul>
 *                 </li>
 *             </ul>
 *             </li>
 *         </ul>
 *         </td>
 *     </tr>
 * 
 *     <tr>
 *         <td style="vertical-align: top;">
 *         <a class="typeNameLink" href="{@docRoot}/scheduler/model/City.html">City</a>
 *         <ul class="inheritance">
 *             <li>
 *             <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *             </li>
 *         </ul>
 *         </td>
 *         <td style="vertical-align: top;">
 *         <a class="typeNameLink" href="{@docRoot}/scheduler/model/CityRecord.html">CityRecord</a>
 *         <ul class="inheritance">
 *             <li>
 *             <a href="{@docRoot}/scheduler/model/DataRecord.html">DataRecord</a>
 *             <ul class="inheritance">
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                 </li>
 *             </ul>
 *             </li>
 *             <li>
 *             <a href="{@docRoot}/scheduler/model/City.html">City</a>
 *             <ul class="inheritance">
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                 </li>
 *             </ul>
 *             </li>
 *         </ul>
 *         </td>
 *         <td style="vertical-align: top; background-color:#cccccc;">
 *         <a class="typeNameLink" href="{@docRoot}/scheduler/dao/ICityDAO.html">ICityDAO</a>
 *         <ul class="inheritance">
 *             <li>
 *             <a href="{@docRoot}/scheduler/daoDbObject.html">DAO</a>
 *             <ul class="inheritance">
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                 </li>
 *             </ul>
 *             </li>
 *             <li>
 *             <a href="{@docRoot}/scheduler/model/City.html">City</a>
 *             <ul class="inheritance">
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                 </li>
 *             </ul>
 *             </li>
 *         </ul>
 *         </td>
 *         <td style="vertical-align: top; background-color:#cccccc;">
 *         <a class="typeNameLink" href="{@docRoot}/scheduler/dao/CityDAO.html">CityDAO</a>
 *         <ul class="inheritance">
 *             <li>
 *             <a href="{@docRoot}/scheduler/dao/DataAccessObject.html">DataAccessObject</a>
 *             <ul class="inheritance">
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/dao/DbRecord.html">DbRecord</a>
 *                 <ul class="inheritance">
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/daoDbObject.html">DAO</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/model/DataRecord.html">DataRecord</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                 </ul>
 *                 </li>
 *             </ul>
 *             </li>
 *             <li>
 *             <a href="{@docRoot}/scheduler/dao/CityDbRecord.html">CityDbRecord</a>
 *             <ul class="inheritance">
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/dao/ICityDAO.html">ICityDAO</a>
 *                 <ul class="inheritance">
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/daoDbObject.html">DAO</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/model/City.html">City</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                 </ul>
 *                 </li>
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/dao/DbRecord.html">DbRecord</a>
 *                 <ul class="inheritance">
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/daoDbObject.html">DAO</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/model/DataRecord.html">DataRecord</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                 </ul>
 *                 </li>
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/model/CityRecord.html">CityRecord</a>
 *                 <ul class="inheritance">
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/model/DataRecord.html">DataRecord</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/model/City.html">City</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                 </ul>
 *                 </li>
 *             </ul>
 *             </li>
 *         </ul>
 *         </td>
 *         <td style="vertical-align: top;">
 *         <a class="typeNameLink" href="{@docRoot}/scheduler/model/ui/CityItem.html">CityItem</a>
 *         <ul class="inheritance">
 *             <li>
 *             <a href="{@docRoot}/scheduler/model/City.html">City</a>
 *             <ul class="inheritance">
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                 </li>
 *             </ul>
 *             </li>
 *             <li>
 *             <a href="{@docRoot}/scheduler/model/ui/FxModel.html">FxModel</a>
 *             <ul class="inheritance">
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                 </li>
 *             </ul>
 *             </li>
 *         </ul>
 *         </td>
 *         <td style="vertical-align: top;">
 *         <a class="typeNameLink" href="{@docRoot}/scheduler/model/ui/CityModel.html">CityModel</a>
 *         <ul class="inheritance">
 *             <li>
 *             <a href="{@docRoot}/scheduler/model/ui/FxRecordModel.html">FxRecordModel</a>
 *             <ul class="inheritance">
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/model/ui/IFxRecordModel.html">IFxRecordModel</a>
 *                 <ul class="inheritance">
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/model/ui/FxDbModel.html">FxDbModel</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/ui/FxModel.html">FxModel</a>
 *                         <ul class="inheritance">
 *                             <li>
 *                             <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                             </li>
 *                         </ul>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/model/DataRecord.html">DataRecord</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                 </ul>
 *                 </li>
 *             </ul>
 *             </li>
 *             <li>
 *             <a href="{@docRoot}/scheduler/model/ui/CityDbItem.html">CityDbItem</a>
 *             <ul class="inheritance">
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/model/ui/CityItem.html">CityItem</a>
 *                 <ul class="inheritance">
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/model/City.html">City</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/model/ui/FxModel.html">FxModel</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                 </ul>
 *                 </li>
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/model/ui/FxDbModel.html">FxDbModel</a>
 *                 <ul class="inheritance">
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/model/ui/FxModel.html">FxModel</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                 </ul>
 *                 </li>
 *             </ul>
 *             </li>
 *         </ul>
 *         </td>
 *     </tr>
 * 
 *     <tr>
 *         <td style="vertical-align: top;">
 *         <a class="typeNameLink" href="{@docRoot}/scheduler/model/Country.html">Country</a>
 *         <ul class="inheritance">
 *             <li>
 *             <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *             </li>
 *         </ul>
 *         </td>
 *         <td style="vertical-align: top;">
 *         <a class="typeNameLink" href="{@docRoot}/scheduler/model/CountryRecord.html">CountryRecord</a>
 *         <ul class="inheritance">
 *             <li>
 *             <a href="{@docRoot}/scheduler/model/DataRecord.html">DataRecord</a>
 *             <ul class="inheritance">
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                 </li>
 *             </ul>
 *             </li>
 *             <li>
 *             <a href="{@docRoot}/scheduler/model/Country.html">Country</a>
 *             <ul class="inheritance">
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                 </li>
 *             </ul>
 *             </li>
 *         </ul>
 *         </td>
 *         <td style="vertical-align: top; background-color:#cccccc;">
 *         <a class="typeNameLink" href="{@docRoot}/scheduler/dao/ICountryDAO.html">ICountryDAO</a>
 *         <ul class="inheritance">
 *             <li>
 *             <a href="{@docRoot}/scheduler/daoDbObject.html">DAO</a>
 *             <ul class="inheritance">
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                 </li>
 *             </ul>
 *             </li>
 *             <li>
 *             <a href="{@docRoot}/scheduler/model/Country.html">Country</a>
 *             <ul class="inheritance">
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                 </li>
 *             </ul>
 *             </li>
 *         </ul>
 *         </td>
 *         <td style="vertical-align: top; background-color:#cccccc;">
 *         <a class="typeNameLink" href="{@docRoot}/scheduler/dao/CountryDAO.html">CountryDAO</a>
 *         <ul class="inheritance">
 *             <li>
 *             <a href="{@docRoot}/scheduler/dao/DataAccessObject.html">DataAccessObject</a>
 *             <ul class="inheritance">
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/dao/DbRecord.html">DbRecord</a>
 *                 <ul class="inheritance">
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/daoDbObject.html">DAO</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/model/DataRecord.html">DataRecord</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                 </ul>
 *                 </li>
 *             </ul>
 *             </li>
 *             <li>
 *             <a href="{@docRoot}/scheduler/dao/CountryDbRecord.html">CountryDbRecord</a>
 *             <ul class="inheritance">
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/dao/ICountryDAO.html">ICountryDAO</a>
 *                 <ul class="inheritance">
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/daoDbObject.html">DAO</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/model/Country.html">Country</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                 </ul>
 *                 </li>
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/dao/DbRecord.html">DbRecord</a>
 *                 <ul class="inheritance">
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/daoDbObject.html">DAO</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/model/DataRecord.html">DataRecord</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                 </ul>
 *                 </li>
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/model/CountryRecord.html">CountryRecord</a>
 *                 <ul class="inheritance">
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/model/DataRecord.html">DataRecord</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/model/Country.html">Country</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                 </ul>
 *                 </li>
 *             </ul>
 *             </li>
 *         </ul>
 *         </td>
 *         <td style="vertical-align: top;">
 *         <a class="typeNameLink" href="{@docRoot}/scheduler/model/ui/CountryItem.html">CountryItem</a>
 *         <ul class="inheritance">
 *             <li>
 *             <a href="{@docRoot}/scheduler/model/Country.html">Country</a>
 *             <ul class="inheritance">
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                 </li>
 *             </ul>
 *             </li>
 *             <li>
 *             <a href="{@docRoot}/scheduler/model/ui/FxModel.html">FxModel</a>
 *             <ul class="inheritance">
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                 </li>
 *             </ul>
 *             </li>
 *         </ul>
 *         </td>
 *         <td style="vertical-align: top;">
 *         <a class="typeNameLink" href="{@docRoot}/scheduler/model/ui/CountryModel.html">CountryModel</a>
 *         <ul class="inheritance">
 *             <li>
 *             <a href="{@docRoot}/scheduler/model/ui/FxRecordModel.html">FxRecordModel</a>
 *             <ul class="inheritance">
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/model/ui/IFxRecordModel.html">IFxRecordModel</a>
 *                 <ul class="inheritance">
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/model/ui/FxDbModel.html">FxDbModel</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/ui/FxModel.html">FxModel</a>
 *                         <ul class="inheritance">
 *                             <li>
 *                             <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                             </li>
 *                         </ul>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/model/DataRecord.html">DataRecord</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                 </ul>
 *                 </li>
 *             </ul>
 *             </li>
 *             <li>
 *             <a href="{@docRoot}/scheduler/model/ui/CountryDbItem.html">CountryDbItem</a>
 *             <ul class="inheritance">
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/model/ui/CountryItem.html">CountryItem</a>
 *                 <ul class="inheritance">
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/model/Country.html">Country</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/model/ui/FxModel.html">FxModel</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                 </ul>
 *                 </li>
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/model/ui/FxDbModel.html">FxDbModel</a>
 *                 <ul class="inheritance">
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/model/ui/FxModel.html">FxModel</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                 </ul>
 *                 </li>
 *             </ul>
 *             </li>
 *         </ul>
 *         </td>
 *     </tr>
 * 
 *     <tr>
 *         <td style="vertical-align: top;">
 *         <a class="typeNameLink" href="{@docRoot}/scheduler/model/Customer.html">Customer</a>
 *         <ul class="inheritance">
 *             <li>
 *             <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *             </li>
 *         </ul>
 *         </td>
 *         <td style="vertical-align: top;">
 *         <a class="typeNameLink" href="{@docRoot}/scheduler/model/CustomerRecord.html">CustomerRecord</a>
 *         <ul class="inheritance">
 *             <li>
 *             <a href="{@docRoot}/scheduler/model/DataRecord.html">DataRecord</a>
 *             <ul class="inheritance">
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                 </li>
 *             </ul>
 *             </li>
 *             <li>
 *             <a href="{@docRoot}/scheduler/model/Customer.html">Customer</a>
 *             <ul class="inheritance">
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                 </li>
 *             </ul>
 *             </li>
 *         </ul>
 *         </td>
 *         <td style="vertical-align: top; background-color:#cccccc;">
 *         <a class="typeNameLink" href="{@docRoot}/scheduler/dao/ICustomerDAO.html">ICustomerDAO</a>
 *         <ul class="inheritance">
 *             <li>
 *             <a href="{@docRoot}/scheduler/daoDbObject.html">DAO</a>
 *             <ul class="inheritance">
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                 </li>
 *             </ul>
 *             </li>
 *             <li>
 *             <a href="{@docRoot}/scheduler/model/Customer.html">Customer</a>
 *             <ul class="inheritance">
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                 </li>
 *             </ul>
 *             </li>
 *         </ul>
 *         </td>
 *         <td style="vertical-align: top; background-color:#cccccc;">
 *         <a class="typeNameLink" href="{@docRoot}/scheduler/dao/CustomerDAO.html">CustomerDAO</a>
 *         <ul class="inheritance">
 *             <li>
 *             <a href="{@docRoot}/scheduler/dao/DataAccessObject.html">DataAccessObject</a>
 *             <ul class="inheritance">
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/dao/DbRecord.html">DbRecord</a>
 *                 <ul class="inheritance">
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/daoDbObject.html">DAO</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/model/DataRecord.html">DataRecord</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                 </ul>
 *                 </li>
 *             </ul>
 *             </li>
 *             <li>
 *             <a href="{@docRoot}/scheduler/dao/CustomerDbRecord.html">CustomerDbRecord</a>
 *             <ul class="inheritance">
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/dao/ICustomerDAO.html">ICustomerDAO</a>
 *                 <ul class="inheritance">
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/daoDbObject.html">DAO</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/model/Customer.html">Customer</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                 </ul>
 *                 </li>
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/dao/DbRecord.html">DbRecord</a>
 *                 <ul class="inheritance">
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/daoDbObject.html">DAO</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/model/DataRecord.html">DataRecord</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                 </ul>
 *                 </li>
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/model/CustomerRecord.html">CustomerRecord</a>
 *                 <ul class="inheritance">
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/model/DataRecord.html">DataRecord</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/model/Customer.html">Customer</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                 </ul>
 *                 </li>
 *             </ul>
 *             </li>
 *         </ul>
 *         </td>
 *         <td style="vertical-align: top;">
 *         <a class="typeNameLink" href="{@docRoot}/scheduler/model/ui/CustomerItem.html">CustomerItem</a>
 *         <ul class="inheritance">
 *             <li>
 *             <a href="{@docRoot}/scheduler/model/Customer.html">Customer</a>
 *             <ul class="inheritance">
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                 </li>
 *             </ul>
 *             </li>
 *             <li>
 *             <a href="{@docRoot}/scheduler/model/ui/FxModel.html">FxModel</a>
 *             <ul class="inheritance">
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                 </li>
 *             </ul>
 *             </li>
 *         </ul>
 *         </td>
 *         <td style="vertical-align: top;">
 *         <a class="typeNameLink" href="{@docRoot}/scheduler/model/ui/CustomerModel.html">CustomerModel</a>
 *         <ul class="inheritance">
 *             <li>
 *             <a href="{@docRoot}/scheduler/model/ui/FxRecordModel.html">FxRecordModel</a>
 *             <ul class="inheritance">
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/model/ui/IFxRecordModel.html">IFxRecordModel</a>
 *                 <ul class="inheritance">
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/model/ui/FxDbModel.html">FxDbModel</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/ui/FxModel.html">FxModel</a>
 *                         <ul class="inheritance">
 *                             <li>
 *                             <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                             </li>
 *                         </ul>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/model/DataRecord.html">DataRecord</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                 </ul>
 *                 </li>
 *             </ul>
 *             </li>
 *             <li>
 *             <a href="{@docRoot}/scheduler/model/ui/CustomerDbItem.html">CustomerDbItem</a>
 *             <ul class="inheritance">
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/model/ui/CustomerItem.html">CustomerItem</a>
 *                 <ul class="inheritance">
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/model/Customer.html">Customer</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/model/ui/FxModel.html">FxModel</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                 </ul>
 *                 </li>
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/model/ui/FxDbModel.html">FxDbModel</a>
 *                 <ul class="inheritance">
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/model/ui/FxModel.html">FxModel</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                 </ul>
 *                 </li>
 *             </ul>
 *             </li>
 *         </ul>
 *         </td>
 *     </tr>
 * 
 *     <tr>
 *         <td style="vertical-align: top;">
 *         <a class="typeNameLink" href="{@docRoot}/scheduler/model/User.html">User</a>
 *         <ul class="inheritance">
 *             <li>
 *             <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *             </li>
 *         </ul>
 *         </td>
 *         <td style="vertical-align: top;">
 *         <a class="typeNameLink" href="{@docRoot}/scheduler/model/UserRecord.html">UserRecord</a>
 *         <ul class="inheritance">
 *             <li>
 *             <a href="{@docRoot}/scheduler/model/DataRecord.html">DataRecord</a>
 *             <ul class="inheritance">
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                 </li>
 *             </ul>
 *             </li>
 *             <li>
 *             <a href="{@docRoot}/scheduler/model/User.html">User</a>
 *             <ul class="inheritance">
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                 </li>
 *             </ul>
 *             </li>
 *         </ul>
 *         </td>
 *         <td style="vertical-align: top; background-color:#cccccc;">
 *         <a class="typeNameLink" href="{@docRoot}/scheduler/dao/IUserDAO.html">IUserDAO</a>
 *         <ul class="inheritance">
 *             <li>
 *             <a href="{@docRoot}/scheduler/daoDbObject.html">DAO</a>
 *             <ul class="inheritance">
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                 </li>
 *             </ul>
 *             </li>
 *             <li>
 *             <a href="{@docRoot}/scheduler/model/User.html">User</a>
 *             <ul class="inheritance">
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                 </li>
 *             </ul>
 *             </li>
 *         </ul>
 *         </td>
 *         <td style="vertical-align: top; background-color:#cccccc;">
 *         <a class="typeNameLink" href="{@docRoot}/scheduler/dao/UserDAO.html">UserDAO</a>
 *         <ul class="inheritance">
 *             <li>
 *             <a href="{@docRoot}/scheduler/dao/DataAccessObject.html">DataAccessObject</a>
 *             <ul class="inheritance">
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/dao/DbRecord.html">DbRecord</a>
 *                 <ul class="inheritance">
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/daoDbObject.html">DAO</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/model/DataRecord.html">DataRecord</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                 </ul>
 *                 </li>
 *             </ul>
 *             </li>
 *             <li>
 *             <a href="{@docRoot}/scheduler/dao/UserDbRecord.html">UserDbRecord</a>
 *             <ul class="inheritance">
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/dao/IUserDAO.html">IUserDAO</a>
 *                 <ul class="inheritance">
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/daoDbObject.html">DAO</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/model/User.html">User</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                 </ul>
 *                 </li>
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/dao/DbRecord.html">DbRecord</a>
 *                 <ul class="inheritance">
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/daoDbObject.html">DAO</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/model/DataRecord.html">DataRecord</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                 </ul>
 *                 </li>
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/model/UserRecord.html">UserRecord</a>
 *                 <ul class="inheritance">
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/model/DataRecord.html">DataRecord</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/model/User.html">User</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                 </ul>
 *                 </li>
 *             </ul>
 *             </li>
 *         </ul>
 *         </td>
 *         <td style="vertical-align: top;">
 *         <a class="typeNameLink" href="{@docRoot}/scheduler/model/ui/UserItem.html">UserItem</a>
 *         <ul class="inheritance">
 *             <li>
 *             <a href="{@docRoot}/scheduler/model/User.html">User</a>
 *             <ul class="inheritance">
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                 </li>
 *             </ul>
 *             </li>
 *             <li>
 *             <a href="{@docRoot}/scheduler/model/ui/FxModel.html">FxModel</a>
 *             <ul class="inheritance">
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                 </li>
 *             </ul>
 *             </li>
 *         </ul>
 *         </td>
 *         <td style="vertical-align: top;">
 *         <a class="typeNameLink" href="{@docRoot}/scheduler/model/ui/UserModel.html">UserModel</a>
 *         <ul class="inheritance">
 *             <li>
 *             <a href="{@docRoot}/scheduler/model/ui/FxRecordModel.html">FxRecordModel</a>
 *             <ul class="inheritance">
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/model/ui/IFxRecordModel.html">IFxRecordModel</a>
 *                 <ul class="inheritance">
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/model/ui/FxDbModel.html">FxDbModel</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/ui/FxModel.html">FxModel</a>
 *                         <ul class="inheritance">
 *                             <li>
 *                             <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                             </li>
 *                         </ul>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/model/DataRecord.html">DataRecord</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                 </ul>
 *                 </li>
 *             </ul>
 *             </li>
 *             <li>
 *             <a href="{@docRoot}/scheduler/model/ui/UserDbItem.html">UserDbItem</a>
 *             <ul class="inheritance">
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/model/ui/UserItem.html">UserItem</a>
 *                 <ul class="inheritance">
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/model/User.html">User</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/model/ui/FxModel.html">FxModel</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                 </ul>
 *                 </li>
 *                 <li>
 *                 <a href="{@docRoot}/scheduler/model/ui/FxDbModel.html">FxDbModel</a>
 *                 <ul class="inheritance">
 *                     <li>
 *                     <a href="{@docRoot}/scheduler/model/ui/FxModel.html">FxModel</a>
 *                     <ul class="inheritance">
 *                         <li>
 *                         <a href="{@docRoot}/scheduler/model/DataObject.html">DataObject</a>
 *                         </li>
 *                     </ul>
 *                     </li>
 *                 </ul>
 *                 </li>
 *             </ul>
 *             </li>
 *         </ul>
 *         </td>
 *     </tr>
 *   </tbody>
 * </table>
 */
package scheduler.model;
