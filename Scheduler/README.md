# Task Stream Submission Notes

## Scenario

> You are working for a software company that has been contracted to develop a scheduling desktop user interface application. The contract is with a global consulting organization that conducts business in multiple languages and has main offices in Phoenix, Arizona; New York, New York; and London, England. The consulting organization has provided a MySQL database that your application must pull data from. The database is used for other systems and therefore its structure cannot be modified.
>
> The organization outlined specific business requirements that must be included as part of the application. From these requirements, a system analyst at your company created solution statements for you to implement in developing the application. These statements are listed in the requirements section.

### Main offices

Names and Addresses for main offices are stored in the XML-formatted resource file [resources\scheduler\StaticAddresses.xml](resources\scheduler\StaticAddresses.xml). This file is loaded and parsed by [src\scheduler\model\PredefinedData.java](src\scheduler\model\PredefinedData.java). These addresses will appear as selectable options in the [Appointment Edit Control](src\scheduler\view\appointment\EditAppointment.java) when the [Corporate Location Appointment Type](src\scheduler\model\AppointmentType.java) is selected.

## A:LOG-IN FORM

> Create a log-in form that can determine the user’s location and translate log-in and error control messages (e.g., “The username and password did not match.”) into two languages.

The [Login Control](src\scheduler\view\Login.java) detects which option from the [Supported Locale enumeration](src\scheduler\SupportedLocale.java) best matches the default system locale. A combobox
is also presented upon login in the event that the user's current locale is not among any of the supported locales.

## B:CUSTOMER RECORDS

> Provide the ability to add, update, and delete customer records in the database, including name, address, and phone number.

The "Customer" menu drop-down can be used to add, update, and delete customer records.

## C:APPOINTMENTS

> Provide the ability to add, update, and delete appointments, capturing the type of appointment and a link to the specific customer record in the database.

The "Appointments" menu drop-down can be used to add, update, and delete appointments.
In the appointment edit screen, there is an "Open" button just above the customer selection drop-down, which can be used to open the selected customer.

## D:CALENDAR VIEWS

> Provide the ability to view the calendar by month and by week.

The "Appointments" menu drop-down includes menu items for "Weekly Calendar" and "Monthly Calendar".

## E:TIME ZONES

> Provide the ability to automatically adjust appointment times based on user time zones and daylight saving time.

Dates and times are stored as GMT in the database and are converted to the current local time zone when retrieved from the database.

## F:EXCEPTION CONTROL

> Write exception controls to prevent each of the following. You may use the same mechanism of exception control more than once, but you must incorporate at least two different mechanisms of exception control.
>
> - scheduling an appointment outside business hours

The [Edit Appointment Control](src\scheduler\view\appointment\EditAppointment.java) checks against the application-defined business hours and prompts the user with a confirmation dialog box before submitting appointments outside of business houirs.

> - scheduling overlapping appointments

The [Edit Appointment Control](src\scheduler\view\appointment\EditAppointment.java) has a button which can check for conflicting appointment dates before saving. Also, the user is prompted to confirm whether to accept any conflicts.

> - entering nonexistent or invalid customer data

The [Edit Customer Control](src\scheduler\view\customer\EditCustomer.java) has validation bindings to ensure that the customer name is not empty and that an address is specified. The "save" button is not enabled until all fields are valid. All other edit controls use combo-boxes to select customers, to ensure that an invalid customer is not specified.

> - entering an incorrect username and password

The [Login Control](src\scheduler\view\Login.java) displays a message if the user enters a username and password that does not match anything in the database.

## G:LAMBDA EXPRESSIONS

> Write two or more lambda expressions to make your program more efficient, justifying the use of each lambda expression with an in-line comment.

The [Edit Customer Control](src\scheduler\view\customer\EditCustomer.java) has several validation and normalization bindings defined, beginning on line 435.

## H:ALERTS

> Write code to provide an alert if there is an appointment within 15 minutes of the user’s log-in.

A background scheduled task, [Appointment Alert Manager](src\scheduler\AppointmentAlertManager.java), is started upon login and checks every 15 seconds for appointments that have either already started or will start within 15 minutes.
The [Main Controller](src\scheduler\view\MainController.java) listens for this event and displays an alert.

## I:REPORTS

> Provide the ability to generate each  of the following reports:
>
> - number of appointment types by month

This can be viewed by selecting "Reports" -> "Types By Month".

> - the schedule for each consultant

This can be viewed by selecting "Reports" -> "Consultant Schedule".

> - one additional report of your choice

This is a pie chart that can be viewed by selecting "Reports" -> "Appointments by Region".

## J:ACTIVITY LOG

> Provide the ability to track user activity by recording timestamps for user log-ins in a .txt file. Each new record should be appended to the log file, if the file already exists.

A file named log.txt will be created/appended to in the current working directory.

## K. PROFESSIONAL COMMUNICATION:

> Demonstrate professional communication in the content and presentation of your submission.

Efforts were made to make it intuitive and to provide help text as well as using javadoc for code comments.