# Task Stream Submission Notes

# Scenario

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

### NOT EVIDENT

> No code is provided to add, update, or delete customer records.

### APPROACHING COMPETENCE

> The application has limited functionality to add, update, or delete customer records in the database or does not include fields for customer name, address, or phone number. The code contains errors or is incomplete.

### COMPETENT

> The application has functionality to add, update, and delete customer records in the database, including name, address, and phone number. The code is complete and functions properly.

## C:APPOINTMENTS

> Provide the ability to add, update, and delete appointments, capturing the type of appointment and a link to the specific customer record in the database.

### NOT EVIDENT

> No code is provided to add, update, or delete appointments. .

### APPROACHING COMPETENCE

> The application code has limited functionality to add, update or delete appointments, capture the type of appointment, or link the appointments to the specific customer record in the database. The code contains errors or is incomplete.

### COMPETENT

> The application code has functionality to add, update, and delete appointments, capture the type of appointment, and link the appointments to the specific customer record in the database. The code is complete and functions properly.

## D:CALENDAR VIEWS

> Provide the ability to view the calendar by month and by week.

### NOT EVIDENT

> Calendar views are not created, or no code is provided.

### APPROACHING COMPETENCE

> The application has limited functionality to view the calendar by month and by week, but the code contains errors or is incomplete.

### COMPETENT

> The application has functionality to view the calendar by month and by week. The code is complete and functions properly.

## E:TIME ZONES

> Provide the ability to automatically adjust appointment times based on user time zones and daylight saving time.

### NOT EVIDENT

> No code is provided to adjust appointment times based on user time zones or daylight saving time.

### APPROACHING COMPETENCE

> The application has limited functionality to automatically adjust appointment times based on user time zones or daylight saving time. The code contains errors or is incomplete.

### COMPETENT

> The application has functionality to automatically adjust appointment times based on user time zones and daylight saving time. The code is complete and functions properly.

## F:EXCEPTION CONTROL

> Write exception controls to prevent each of the following. You may use the same mechanism of exception control more than once, but you must incorporate at least  two different mechanisms of exception control.
> - scheduling an appointment outside business hours
> - scheduling overlapping appointments
> - entering nonexistent or invalid customer data
> - entering an incorrect username and password

### NOT EVIDENT

> No exception controls are created, or no code is provided.

### APPROACHING COMPETENCE

> The application code includes some exception controls but does not prevent each of the given points or uses only 1 mechanism of exception control. The code contains errors or is incomplete.

### COMPETENT

> The application code includes exception controls to prevent each of the given points and uses at least 2 different mechanisms. The code is complete and functions properly.

# G:LAMBDA EXPRESSIONS

> Write two or more lambda expressions to make your program more efficient, justifying the use of each lambda expression with an in-line comment.

### NOT EVIDENT

> The application code uses fewer than two lambda expressions, or no justification is provided. 

### APPROACHING COMPETENCE

> The application code includes two lambda expressions to make the program more efficient, but the justification of the use of each lambda expression with in-line comments is illogical or the use of lambda expressions is not appropriate. The code contains errors or is incomplete.

### COMPETENT

> The application code includes two or more appropriate lambda expressions to make the program more efficient and provides a logical justification of the use of each lambda expression with in-line comments. The code is complete and functions properly.

## H:ALERTS

> Write code to provide an alert if there is an appointment within 15 minutes of the user’s log-in.

### NOT EVIDENT

> No code is provided for alerts.

### APPROACHING COMPETENCE

> The application has limited functionality to provide an alert if there is an appointment within 15 minutes of the user’s log-in, but the code contains errors or is incomplete.

### COMPETENT

> The application has functionality for an alert if there is an appointment within 15 minutes of the user’s log-in. The code is complete and functions properly.

## I:REPORTS

> Provide the ability to generate each  of the following reports:
> - number of appointment types by month
> - the schedule for each consultant
> - one additional report of your choice

### NOT EVIDENT

> No code is provided to generate reports.

### APPROACHING COMPETENCE

> The application has limited functionality to generate the given reports, but the code contains errors or is incomplete.

### COMPETENT

> The application has functionality to generate each of the given reports. The code is complete and functions properly.

## J:ACTIVITY LOG

> Provide the ability to track user activity by recording timestamps for user log-ins in a .txt file. Each new record should be appended to the log file, if the file already exists.

### NOT EVIDENT

> No code is provided to track user activity in a .txt file.

### APPROACHING COMPETENCE

> The application has limited functionality to track user activity by recording timestamps for user log-ins in a .txt file or each new record creates a new file instead of being appended to the log file if the file already exists. The code contains errors or is incomplete.

### COMPETENT

> The application has functionality to track user activity by recording timestamps for user log-ins in a .txt file, and each new record is appended to the log file if the file already exists. The code is complete and functions properly.

## K. PROFESSIONAL COMMUNICATION:

> Demonstrate professional communication in the content and presentation of your submission.

### NOT EVIDENT

> Content is unstructured, is disjointed, or contains pervasive errors in mechanics, usage, or grammar. Vocabulary or tone is unprofessional or distracts from the topic.

### APPROACHING COMPETENCE

> Content is poorly organized, is difficult to follow, or contains errors in mechanics, usage, or grammar that cause confusion. Terminology is misused or ineffective.

### COMPETENT

> Content reflects attention to detail, is organized, and focuses on the main ideas as prescribed in the task or chosen by the candidate. Terminology is pertinent, is used correctly, and effectively conveys the intended meaning. Mechanics, usage, and grammar promote accurate interpretation and understanding. 


The **"Appointments"**  menu dropdown lets you view listings of appointments.

### Calendar Views

The [Weekly Calendar](#onByWeekAction)
menu item shows a weekly calendar of appointments, and [Monthly Calendar](#onByMonthAction) shows all appointments for a given month.

### Tabular Listings

The [My Current and Upcoming Appointments](#onMyCurrentAndUpcomingAction)
menu item shows your appointments for today as well as upcoming appointments. The [All Appointments](#onAllAppointmentsAction) menu item shows all appointments.

## Adding Appointments

Select ["New"](#onNewAppointmentAction) from the
**"Appointments"** menu

### or

Use the **"Customers"** menu dropdown to open a [customer listing](#onCustomerListingAction). Then, after opening the customer record, and click the
**"Add Appointment"** button at the bottom." />

## Appointment Reporting

From the **"Reports"** menu dropdown, select [Types by Month](#onTypesByMonthAction)
to see a bar chart showing the number of appointments for each apointment type in a given month. Use the [Consultant Schedule](#onConsultantScheduleAction)
to see appointment schedule reports for each consultant. The [Appointments by Region](#onByRegionAction)
menu option allows you to generate a pie chart of the customer nationalities for appointments in a given time period.

## Manage Customers

Use the **"Customers"** menu to view a [list of customers](#onCustomerListingAction)

### or

Use the **"Manage Addresses"** menu to view a [list of countries](#onCountryListingAction).
You can then select the city and address to view customers at that specific address.

## Manage Consultants

Use the **"Consultants"** menu to view a [list of consultants](#onUserListingAction).
