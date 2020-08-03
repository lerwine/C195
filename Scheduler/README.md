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
> - scheduling an appointment outside business hours
> - scheduling overlapping appointments
> - entering nonexistent or invalid customer data
> - entering an incorrect username and password


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
