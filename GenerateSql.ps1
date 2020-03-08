$Script:Random = [System.Random]::new();
$Script:CurrentTime = [System.TimeSpan]::Zero;

Function Get-Random {
    Param(
        [ValidateRange(0, [int]::MaxValue)]
        [double]$Min = 0.0,
        
        [Parameter(Mandatory = $True)]
        [ValidateRange(0, [int]::MaxValue)]
        [double]$Max,

        [switch]$Round
    )

    if ($Round.IsPresent) {
        [Math]::Round($Min + (($Max - $Min) * $Script:Random.NextDouble()), 0);
    } else {
        $Min + (($Max - $Min) * $Script:Random.NextDouble());
    }
}

Function Set-TimeSpan {
    Param(
        [ValidateRange(0, [int]::MaxValue)]
        [int]$MilliSeconds,
        
        [ValidateRange(0, [int]::MaxValue)]
        [double]$Seconds,
        
        [ValidateRange(0, [int]::MaxValue)]
        [double]$Minutes,
        
        [ValidateRange(0, [int]::MaxValue)]
        [double]$Hours,
        
        [ValidateRange(0, [int]::MaxValue)]
        [double]$Days,

        [System.TimeSpan]$From
    )

    $TimeSpan = $Script:CurrentTime;
    if ($PSBoundParameters.ContainsKey('From')) { $TimeSpan = $From; }
    if (-not $PSBoundParameters.ContainsKey('Days')) { [double]$Days = $TimeSpan.Days }
    if (-not $PSBoundParameters.ContainsKey('Hours')) { [double]$Hours = $TimeSpan.Hours }
    if (-not $PSBoundParameters.ContainsKey('Minutes')) { [double]$Minutes = $TimeSpan.Minutes }
    if (-not $PSBoundParameters.ContainsKey('Seconds')) { [double]$Seconds = $TimeSpan.Seconds }
    if (-not $PSBoundParameters.ContainsKey('MilliSeconds')) { $MilliSeconds = $TimeSpan.MilliSeconds }
    [int]$i = [Math]::Floor($Days);
    $r = $Days - ([double]$i);
    if ($r -gt 0) { $Hours += ($r * 24.0) }
    $Days = $i;
    [int]$i = [Math]::Floor($Hours);
    $r = $Hours - ([double]$i);
    if ($r -gt 0) { $Minutes += ($r * 60.0) }
    $Hours = $i;
    [int]$i = [Math]::Floor($Minutes);
    $r = $Minutes - ([double]$i);
    if ($r -gt 0) { $Seconds += ($r * 60.0) }
    $Minutes = $i;
    [int]$i = [Math]::Floor($Seconds);
    $r = $Seconds - ([double]$i);
    if ($r -gt 0) { $MilliSeconds += ($r * 1000.0) }
    $Seconds = $i;
    while ($MilliSeconds -ge 1000) {
        $Seconds++;
        $MilliSeconds -= 1000;
    }
    while ($Seconds -ge 60) {
        $Minutes++;
        $Seconds -= 60;
    }
    while ($Minutes -ge 60) {
        $Hours++;
        $Minutes -= 60;
    }
    while ($Hours -ge 24) {
        $Days++;
        $Hours -= 24;
    }
    New-Object -TypeName 'System.TimeSpan' -ArgumentList $Days, $Hours, $Minutes, $Seconds, $MilliSeconds;
}

Function Get-RelativeTime {
    [CmdletBinding(DefaultParameterSetName = "Add")]
    Param(
        [ValidateRange(0, [int]::MaxValue)]
        [int]$MilliSeconds = 0,
        
        [ValidateRange(0, [int]::MaxValue)]
        [double]$Seconds = 0.0,
        
        [ValidateRange(0, [int]::MaxValue)]
        [double]$Minutes = 0.0,
        
        [ValidateRange(0, [int]::MaxValue)]
        [double]$Hours = 0.0,
        
        [ValidateRange(0, [int]::MaxValue)]
        [double]$Days = 0.0,

        [System.TimeSpan]$From,
        
        [Parameter(ParameterSetName = "Add")]
        [switch]$Add,

        [Parameter(Mandatory = $True, ParameterSetName = "Subtract")]
        [switch]$Subtract
    )
    
    $TimeSpan = $Script:CurrentTime;
    if ($PSBoundParameters.ContainsKey('From')) { $TimeSpan = $From; }
    [int]$i = [Math]::Floor($Days);
    $r = $Days - ([double]$i);
    if ($r -gt 0) { $Hours += ($r * 24.0) }
    $Days = $i;
    [int]$i = [Math]::Floor($Hours);
    $r = $Hours - ([double]$i);
    if ($r -gt 0) { $Minutes += ($r * 60.0) }
    $Hours = $i;
    [int]$i = [Math]::Floor($Minutes);
    $r = $Minutes - ([double]$i);
    if ($r -gt 0) { $Seconds += ($r * 60.0) }
    $Minutes = $i;
    [int]$i = [Math]::Floor($Seconds);
    $r = $Seconds - ([double]$i);
    if ($r -gt 0) { $MilliSeconds += ($r * 1000.0) }
    $Seconds = $i;
    while ($MilliSeconds -ge 1000) {
        $Seconds++;
        $MilliSeconds -= 1000;
    }
    while ($Seconds -ge 60) {
        $Minutes++;
        $Seconds -= 60;
    }
    while ($Minutes -ge 60) {
        $Hours++;
        $Minutes -= 60;
    }
    while ($Hours -ge 24) {
        $Days++;
        $Hours -= 24;
    }
    if ($Subtract) {
        $TimeSpan.Subtract((New-Object -TypeName 'System.TimeSpan' -ArgumentList $Days, $Hours, $Minutes, $Seconds, $MilliSeconds));
    } else {
        $TimeSpan.Add((New-Object -TypeName 'System.TimeSpan' -ArgumentList $Days, $Hours, $Minutes, $Seconds, $MilliSeconds));
    }
}

Function Move-CurrentTime {
    Param(
        [ValidateRange(0, [int]::MaxValue)]
        [int]$MilliSeconds = 0,
        
        [ValidateRange(0, [int]::MaxValue)]
        [double]$Seconds = 0.0,
        
        [ValidateRange(0, [int]::MaxValue)]
        [double]$Minutes = 0.0,
        
        [ValidateRange(0, [int]::MaxValue)]
        [double]$Hours = 0.0,
        
        [ValidateRange(0, [int]::MaxValue)]
        [double]$Days = 0.0
    )

    $Script:CurrentTime = Get-RelativeTime -MilliSeconds $MilliSeconds -Seconds $Seconds -Minutes $Minutes -Hours $Hours -Days $Days;
    $Script:CurrentTime | Write-Output;
}

Function Get-User {
    Param(
        [Parameter(Mandatory = $true, ParameterSetName = 'id')]
        [int]$Id,
        [Parameter(Mandatory = $true, ParameterSetName = 'name')]
        [String]$Name
    )

    if ($PSBoundParameters.ContainsKey('Id')) {
        if ($Id -ge 1 -and $Id -le $Script:Tables.user.all.Count) {
            return $Script:Tables.user.all[$Id - 1];
        }
    } else {
        if ($Script:Tables.user.byName.ContainsKey($Name)) {
            return $Script:Tables.user.byName[$Name];
        }
    }
}

Function Test-User {
    Param(
        [Parameter(Mandatory = $true, ValueFromPipeline = $true)]
        [object]$Obj
    )

    Begin { $Passed = $true; }

    Process {
        if ($Passed) {
            if ($Obj.userId -isnot [int]) {
                $Passed = $false;
            } else {
                $User = Get-User -Id $Obj.userId;
                if ($null -eq $User -or -not [Object]::ReferenceEquals($Obj, $User)) {
                    $Passed = $false;
                }
            }
        }
    }

    End { $Passed | Write-Output }
}

Function Add-User {
    Param(
        [Parameter(Mandatory = $true)]
        [ValidateScript({ -not $Tables.user.byName.ContainsKey($_) })]
        [string]$Name,
        
        [Parameter(Mandatory = $true)]
        [string]$Password,
        
        [ValidateSet('Admin', 'Normal', 'Inactive')]
        [string]$Status = 'Normal',
        
        [ValidateScript({ $_ | Test-User })]
        [Object]$CreatedBy
    )
    
    $StatusValue = 1;
    if ($Status -eq 'Admin') {
        $StatusValue = 2;
    } else {
        if ($Status -eq 'Inactive') { $StatusValue = 0 }
    }
    
    $User = New-Object -TypeName 'System.Management.Automation.PSObject' -Property @{
        userId = $Script:Tables.user.all.Count + 1;
        userName = $Name;
        password = $Password;
        active = $StatusValue;
        createDate = $Script:EventTimes.Count;
        createdBy = $CreatedBy.userName;
        lastUpdate = $Script:EventTimes.Count;
        lastUpdateBy = $CreatedBy.userName;
    };
    $Script:EventTimes.Add($Script:CurrentTime);
    $Script:Tables.user.all.Add($User);
    $Script:Tables.user.byName.Add($Name, $User);
    $User | Write-Output;
}

Function Get-Country {
    Param(
        [Parameter(Mandatory = $true, ParameterSetName = 'id')]
        [int]$Id,
        [Parameter(Mandatory = $true, ParameterSetName = 'name')]
        [String]$Name
    )

    if ($PSBoundParameters.ContainsKey('Id')) {
        if ($Id -ge 1 -and $Id -le $Script:Tables.country.all.Count) {
            return $Script:Tables.country.all[$Id - 1];
        }
    } else {
        if ($Script:Tables.country.byName.ContainsKey($Name)) {
            return $Script:Tables.country.byName[$Name];
        }
    }
}

Function Test-Country {
    Param(
        [Parameter(Mandatory = $true, ValueFromPipeline = $true)]
        [object]$Obj
    )

    Begin { $Passed = $true; }

    Process {
        if ($Passed) {
            if ($Obj.countryId -isnot [int]) {
                $Passed = $false;
            } else {
                $Country = Get-Country -Id $Obj.countryId;
                if ($null -eq $Country -or -not [Object]::ReferenceEquals($Obj, $Country)) {
                    $Passed = $false;
                }
            }
        }
    }

    End { $Passed | Write-Output }
}

Function Add-Country {
    Param(
        [Parameter(Mandatory = $true)]
        [ValidateScript({ -not $Tables.country.byName.ContainsKey($_) })]
        [string]$Name,
        
        [ValidateScript({ $_ | Test-User })]
        [Object]$CreatedBy
    )
    $Country = New-Object -TypeName 'System.Management.Automation.PSObject' -Property @{
        countryId = $Script:Tables.country.all.Count + 1;
        country = $Name;
        createDate = $Script:EventTimes.Count;
        createdBy = $CreatedBy.userName;
        lastUpdate = $Script:EventTimes.Count;
        lastUpdateBy = $CreatedBy.userName;
        cityNames = @{};
    };
    $Script:EventTimes.Add($Script:CurrentTime);
    $Script:Tables.country.all.Add($Country);
    $Script:Tables.country.byName.Add($Name, $Country);
    $Country | Write-Output;
}

Function Get-City {
    Param(
        [Parameter(Mandatory = $true, ParameterSetName = 'id')]
        [int]$Id,
        [Parameter(Mandatory = $true, ParameterSetName = 'name')]
        [String]$Name,
        
        [Parameter(Mandatory = $true, ParameterSetName = 'name')]
        [ValidateScript({ $_ | Test-Country })]
        [Object]$Country
    )

    if ($PSBoundParameters.ContainsKey('Id')) {
        if ($Id -ge 1 -and $Id -le $Script:Tables.city.Count) {
            return $Script:Tables.city[$Id - 1];
        }
    } else {
        if ($Country.cityNames.ContainsKey($Name)) {
            return $Country.cityNames[$Name];
        }
    }
}

Function Test-City {
    Param(
        [Parameter(Mandatory = $true, ValueFromPipeline = $true)]
        [object]$Obj
    )

    Begin { $Passed = $true; }

    Process {
        if ($Passed) {
            if ($Obj.cityId -isnot [int]) {
                $Passed = $false;
            } else {
                $City = Get-City -Id $Obj.cityId;
                if ($null -eq $City -or -not [Object]::ReferenceEquals($Obj, $City)) {
                    $Passed = $false;
                }
            }
        }
    }

    End { $Passed | Write-Output }
}

Function Add-City {
    Param(
        [Parameter(Mandatory = $true)]
        [string]$Name,
        
        [Parameter(Mandatory = $true)]
        [ValidateScript({ $_ | Test-Country })]
        [Object]$Country,
        
        [Parameter(Mandatory = $true)]
        [ValidateScript({ $_ | Test-User })]
        [Object]$CreatedBy
    )
    if ($Country.cityNames.ContainsKey($Name)) {
        throw 'A city with that name in that country already exists';
    } else {
        $City = New-Object -TypeName 'System.Management.Automation.PSObject' -Property @{
            cityId = $Script:Tables.city.Count + 1;
            city = $Name;
            countryId = $Country.countryId;
            createDate = $Script:EventTimes.Count;
            createdBy = $CreatedBy.userName;
            lastUpdate = $Script:EventTimes.Count;
            lastUpdateBy = $CreatedBy.userName;
            country = $Country;
        };
    $Script:EventTimes.Add($Script:CurrentTime);
        $Script:Tables.city.Add($City);
        $Country.cityNames.Add($Name, $City);
        $City | Write-Output;
    }
}

Function Get-Address {
    Param(
        [Parameter(Mandatory = $true)]
        [int]$Id
    )

    if ($Id -ge 1 -and $Id -le $Script:Tables.address.Count) {
        return $Script:Tables.address[$Id - 1];
    }
}

Function Test-Address {
    Param(
        [Parameter(Mandatory = $true, ValueFromPipeline = $true)]
        [object]$Obj
    )

    Begin { $Passed = $true; }

    Process {
        if ($Passed) {
            if ($Obj.addressId -isnot [int]) {
                $Passed = $false;
            } else {
                $Address = Get-Address -Id $Obj.addressId;
                if ($null -eq $Address -or -not [Object]::ReferenceEquals($Obj, $Address)) {
                    $Passed = $false;
                }
            }
        }
    }

    End { $Passed | Write-Output }
}

Function Add-Address {
    Param(
        [Parameter(Mandatory = $true)]
        [string]$Street,
        
        [string]$Line2 = '',
        
        [Parameter(Mandatory = $true)]
        [ValidateScript({ $_ | Test-City })]
        [Object]$City,
        
        [Parameter(Mandatory = $true)]
        [string]$PostalCode,
        
        [Parameter(Mandatory = $true)]
        [string]$Phone,
        
        [Parameter(Mandatory = $true)]
        [ValidateScript({ $_ | Test-User })]
        [Object]$CreatedBy
    )
    $Address = New-Object -TypeName 'System.Management.Automation.PSObject' -Property @{
        addressId = $Script:Tables.address.Count + 1;
        address = $Street;
        address2 = $Line2;
        cityId = $City.cityId;
        city = $City;
        postalCode = $PostalCode;
        phone = $Phone;
        createDate = $Script:EventTimes.Count;
        createdBy = $CreatedBy.userName;
        lastUpdate = $Script:EventTimes.Count;
        lastUpdateBy = $CreatedBy.userName;
    };
    $Script:EventTimes.Add($Script:CurrentTime);
    $Script:Tables.address.Add($Address);
    $Address | Write-Output;
}

Function Get-Customer {
    Param(
        [Parameter(Mandatory = $true, ParameterSetName = 'id')]
        [int]$Id,
        [Parameter(Mandatory = $true, ParameterSetName = 'name')]
        [String]$Name
    )

    if ($PSBoundParameters.ContainsKey('Id')) {
        if ($Id -ge 1 -and $Id -le $Script:Tables.customer.all.Count) {
            return $Script:Tables.customer.all[$Id - 1];
        }
    } else {
        if ($Script:Tables.customer.byName.ContainsKey($Name)) {
            return $Script:Tables.customer.byName[$Name];
        }
    }
}

Function Test-Customer {
    Param(
        [Parameter(Mandatory = $true, ValueFromPipeline = $true)]
        [object]$Obj
    )

    Begin { $Passed = $true; }

    Process {
        if ($Passed) {
            if ($Obj.customerId -isnot [int]) {
                $Passed = $false;
            } else {
                $Customer = Get-Customer -Id $Obj.customerId;
                if ($null -eq $Customer -or -not [Object]::ReferenceEquals($Obj, $Customer)) {
                    $Passed = $false;
                }
            }
        }
    }

    End { $Passed | Write-Output }
}

Function Add-Customer {
    [CmdletBinding(DefaultParameterSetName = 'Active')]
    Param(
        [Parameter(Mandatory = $true)]
        [ValidateScript({ -not $Tables.customer.byName.ContainsKey($_) })]
        [string]$Name,
        
        [ValidateScript({ $_ | Test-Address })]
        [Object]$Address,
        
        [Parameter(ParameterSetName = 'Active')]
        [switch]$Active,
        
        [Parameter(Mandatory = $true, ParameterSetName = 'Inactive')]
        [switch]$Inactive,
        
        [ValidateScript({ $_ | Test-User })]
        [Object]$CreatedBy
    )
    # `customerId, customerName, addressId, active, createDate, createdBy, lastUpdate, lastUpdateBy`
    $Customer = New-Object -TypeName 'System.Management.Automation.PSObject' -Property @{
        customerId = $Script:Tables.customer.all.Count + 1;
        customerName = $Name;
        addressId = $Address.addressId;
        address = $Address;
        active = $Active.IsPresent;
        createDate = $Script:EventTimes.Count;
        createdBy = $CreatedBy.userName;
        lastUpdate = $Script:EventTimes.Count;
        lastUpdateBy = $CreatedBy.userName;
    };
    $Script:EventTimes.Add($Script:CurrentTime);
    $Script:Tables.customer.all.Add($Customer);
    $Script:Tables.customer.byName.Add($Name, $Customer);
    $Customer | Write-Output;
}

Function Get-Appointment {
    Param(
        [Parameter(Mandatory = $true, ParameterSetName = 'id')]
        [int]$Id
    )

    if ($Id -ge 1 -and $Id -le $Script:Tables.appointment.Count) {
        return $Script:Tables.appointment[$Id - 1];
    }
}

Function Test-Appointment {
    Param(
        [Parameter(Mandatory = $true, ValueFromPipeline = $true)]
        [object]$Obj
    )

    Begin { $Passed = $true; }

    Process {
        if ($Passed) {
            if ($Obj.appointmentId -isnot [int]) {
                $Passed = $false;
            } else {
                $Appointment = Get-Appointment -Id $Obj.appointmentId;
                if ($null -eq $Appointment -or -not [Object]::ReferenceEquals($Obj, $Appointment)) {
                    $Passed = $false;
                }
            }
        }
    }

    End { $Passed | Write-Output }
}

Function Add-Appointment {
    [CmdletBinding(DefaultParameterSetName = 'home')]
    Param(
        [Parameter(Mandatory = $true)]
        [ValidateScript({ $_ | Test-Customer })]
        [Object]$Customer,
        
        [Parameter(Mandatory = $true)]
        [ValidateScript({ $_ | Test-User })]
        [Object]$User,
        
        [Parameter(Mandatory = $true)]
        [string]$Title,
        
        [Parameter(Mandatory = $true)]
        [string]$Description,
        
        [string]$Contact = '',
        
        [Parameter(Mandatory = $true)]
        [DateTime]$Start,
        
        [Parameter(Mandatory = $true)]
        [DateTime]$End,
        
        [Parameter(Mandatory = $true, ParameterSetName = 'phone')]
        [String]$Phone,
        
        [Parameter(Mandatory = $true, ParameterSetName = 'other')]
        [String]$Address,
        
        [Parameter(Mandatory = $true, ParameterSetName = 'virtual')]
        [Uri]$Virtual,
        
        [Parameter(Mandatory = $true, ParameterSetName = 'customer')]
        [switch]$CustomerSite,
        
        [Parameter(ParameterSetName = 'home')]
        [switch]$HomeOffice,
        
        [Parameter(Mandatory = $true, ParameterSetName = 'germany')]
        [switch]$Germany,
        
        [Parameter(Mandatory = $true, ParameterSetName = 'india')]
        [switch]$India,
        
        [Parameter(Mandatory = $true, ParameterSetName = 'honduras')]
        [switch]$Honduras,
        
        [Parameter(ParameterSetName = 'phone')]
        [Parameter(ParameterSetName = 'other')]
        [Parameter(ParameterSetName = 'customer')]
        [Parameter(ParameterSetName = 'germany')]
        [Parameter(ParameterSetName = 'india')]
        [Parameter(ParameterSetName = 'honduras')]
        [Parameter(ParameterSetName = 'home')]
        [Uri]$Url,
        
        [ValidateScript({ $_ | Test-User })]
        [Object]$CreatedBy
    )
    # `appointmentId, customerId, userId, title, description, location, contact, type, url, start, end, createDate, createdBy, lastUpdate`,`lastUpdateBy`
    $Properties = @{
        appointmentId = $Script:Tables.user.all.Count + 1;
        customerId = $Customer.customerId;
        customer = $User;
        userId = $User.userId;
        title = $Title;
        description = $Description;
        location = '';
        contact = $Contact;
        type = $PSCmdlet.ParameterSetName;
        url = '';
        start = $Start;
        end = $End;
        createDate = $Script:EventTimes.Count;
        createdBy = $CreatedBy.userName;
        lastUpdate = $Script:EventTimes.Count;
        lastUpdateBy = $CreatedBy.userName;
    };
    $Script:EventTimes.Add($Script:CurrentTime);
    switch ($PSCmdlet.ParameterSetName) {
        'phone' {
            if ($Phone.StartsWith('+')) {
                $Properties['url'] = "tel:$Phone";
            } else {
                $Properties['url'] = "tel:+1-$Phone";
            }
            if ($PSBoundParameters.ContainsKey('url')) {
                $Properties['location'] = $Url.AbsoluteUri;
            }
            break;
        }
        'virtual' {
            $Properties['url'] = $Url.AbsoluteUri;
            break;
        }
        'other' {
            $Properties['location'] = $Address;
            if ($PSBoundParameters.ContainsKey('url')) {
                $Properties['url'] = $Url.AbsoluteUri;
            }
            break;
        }
        default {
            if ($PSBoundParameters.ContainsKey('url')) {
                $Properties['url'] = $Url.AbsoluteUri;
            }
            break;
        }
    }
    $Appointment = New-Object -TypeName 'System.Management.Automation.PSObject' -Property $Properties;
    $Script:Tables.appointment.Add($Appointment);
    $User | Write-Output;
}

$Script:EventTimes = New-Object -TypeName 'System.Collections.ObjectModel.Collection[System.TimeSpan]';

$Script:Tables = New-Object -TypeName 'System.Management.Automation.PSObject' -Property @{
    user = New-Object -TypeName 'System.Management.Automation.PSObject' -Property @{
        all = New-Object -TypeName 'System.Collections.ObjectModel.Collection[System.Object]';
        byName = @{};
    };
    customer = New-Object -TypeName 'System.Management.Automation.PSObject' -Property @{
        all = New-Object -TypeName 'System.Collections.ObjectModel.Collection[System.Object]';
        byName = @{};
    };
    country = New-Object -TypeName 'System.Management.Automation.PSObject' -Property @{
        all = New-Object -TypeName 'System.Collections.ObjectModel.Collection[System.Object]';
        byName = @{};
    };
    city = New-Object -TypeName 'System.Collections.ObjectModel.Collection[System.Object]';
    address = New-Object -TypeName 'System.Collections.ObjectModel.Collection[System.Object]';
    appointment = New-Object -TypeName 'System.Collections.ObjectModel.Collection[System.Object]';
}

$TestUser = New-Object -TypeName 'System.Management.Automation.PSObject' -Property @{
    userId = 1;
    userName = 'test';
    password = 'MZFrVPiO381l+/ZsPSZRuR+JP+PUUFjMR/eIoX38MT/3VUiQxQ';
    active = 2;
    createDate = 0;
    createdBy = 'test';
    lastUpdate = 0;
    lastUpdateBy = 'test';
};
$Script:EventTimes.Add($Script:CurrentTime);
$Script:Tables.user.all.Add($TestUser);
$Script:Tables.user.byName.Add('test', $TestUser);

Move-CurrentTime -Hours (Get-Random -Min 1 -Max 4);
$CountryUSA = Add-Country -Name 'USA' -CreatedBy $TestUser;

Move-CurrentTime -Seconds (Get-Random -Min 4 -Max 60);
$CityDC = Add-City -Name 'Washington DC' -Country $CountryUSA -CreatedBy $TestUser;

Move-CurrentTime -Seconds (Get-Random -Min 4 -Max 45);
$AddressWH = Add-Address -Street '1600 Pennsylvania Ave NW' -Line2 'Oval Office' -City $CityDC -PostalCode '20500' -Phone '(202) 456-1414' -CreatedBy $TestUser;

Move-CurrentTime -Seconds (Get-Random -Min 45 -Max 75);
$CustomerTrump = Add-Customer -Name 'President Trump' -Address $AddressWH -CreatedBy $TestUser;

Move-CurrentTime -Hours (Get-Random -Min 1 -Max 3);
$Start = Set-TimeSpan -MilliSeconds 0 -Seconds -Minutes 0 -From (Get-RelativeTime -Days (Get-Random -Min 4 -Max 12 -Round) -Hours (Get-Random -Min 0 -Max 23 -Round));
Add-Appointment -Customer $CustomerTrump -User $TestUser -Title 'First Event' -Description 'Event 1 DESC' -Contact 'Agent Dan' -Address 'Undisclosed' -Start $Start -End (Get-RelativeTime -Hours 0.5 -From $Start) -CreatedBy $TestUser;

Move-CurrentTime -Hours (Get-Random -Min 2 -Max 4);
$CitySanJose = Add-City -Name 'San Jose' -Country $CountryUSA -CreatedBy $TestUser;

Move-CurrentTime -Seconds (Get-Random -Min 4 -Max 45);
$AddressWalsh = Add-Address -Street '191 N 1st St' -Line2 'Department 1' -City $CitySanJose -PostalCode '95113' -Phone '(408) 882-2100' -CreatedBy $TestUser;

Move-CurrentTime -Seconds (Get-Random -Min 4 -Max 45);
$CustomerWalsh = Add-Customer -Name 'Honorable  Brian C. Walsh' -Address $AddressWalsh -CreatedBy $TestUser;

Move-CurrentTime -Hours (Get-Random -Min 1 -Max 3);
$CountryGermany = Add-Country -Name 'Germany' -CreatedBy $TestUser;

Move-CurrentTime -Seconds (Get-Random -Min 4 -Max 45);
$CityBerlin = Add-City -Name 'Berlin' -Country $CountryGermany -CreatedBy $TestUser;

Move-CurrentTime -Seconds (Get-Random -Min 4 -Max 45);
$CityFrankfurt = Add-City -Name 'Frankfurt' -Country $CountryGermany -CreatedBy $TestUser;

Move-CurrentTime -Seconds (Get-Random -Min 4 -Max 45);
$CountryIndia = Add-Country -Name 'India' -CreatedBy $TestUser;

Move-CurrentTime -Seconds (Get-Random -Min 4 -Max 45);
$CityNewDelhi = Add-City -Name 'New Delhi' -Country $CountryIndia -CreatedBy $TestUser;

Move-CurrentTime -Seconds (Get-Random -Min 4 -Max 45);
$CityBangalore = Add-City -Name 'Bangalore' -Country $CountryIndia -CreatedBy $TestUser;

Move-CurrentTime -Seconds (Get-Random -Min 4 -Max 45);
$CountryPuertoRico = Add-Country -Name 'Puerto Rico' -CreatedBy $TestUser;

Move-CurrentTime -Seconds (Get-Random -Min 4 -Max 45);
$CityVieques = Add-City -Name 'Vieques' -Country $CountryPuertoRico -CreatedBy $TestUser;

Move-CurrentTime -Seconds (Get-Random -Min 4 -Max 45);
$CountryHonduras = Add-Country -Name 'Honduras' -CreatedBy $TestUser;

Move-CurrentTime -Seconds (Get-Random -Min 4 -Max 45);
$CityTegucigalpa = Add-City -Name 'Tegucigalpa' -Country $CountryIndia -CreatedBy $TestUser;

Move-CurrentTime -Minutes (Get-Random -Min 5 -Max 30);
$Start = Set-TimeSpan -MilliSeconds 0 -Seconds -Minutes 0 -From (Get-RelativeTime -Days (Get-Random -Min 4 -Max 12 -Round) -Hours (Get-Random -Min 0 -Max 23 -Round));
Add-Appointment -Customer $CustomerWalsh -User $TestUser -Title 'Second Event' -Description 'Event 2 DESC' -Contact 'Artie Johnson' -CustomerSite -Start $Start -End (Get-RelativeTime -Hours 1 -From $Start) -CreatedBy $TestUser;

$Script:CurrentTime;
$DateTime = [DateTime]::UtcNow.Subtract($Script:CurrentTime);
$DateTime

<#
$Script:Tables.user.all | ForEach-Object {
    @"
INSERT INTO U03vHM.user
	(userId, userName, password, active, createDate, createdBy, lastUpdate, lastUpdateBy)
	VALUES ($($_.userId), '$($_.userName)', '$($_.password)', $($_.active), '$($_.createDate.ToString('yyyy-MM-dd HH:mm:ss'))', '$($_.createdBy)', '$($_.lastUpdate.ToString('yyyy-MM-dd HH:mm:ss'))', '$($_.lastUpdateBy)');
"@
}

$Script:Tables.country.all | ForEach-Object {
    @"
INSERT INTO U03vHM.country
	(countryId, country, createDate, createdBy, lastUpdate, lastUpdateBy)
	VALUES ($($_.countryId), '$($_.country)', '$($_.createDate.ToString('yyyy-MM-dd HH:mm:ss'))', '$($_.createdBy)', '$($_.lastUpdate.ToString('yyyy-MM-dd HH:mm:ss'))', '$($_.lastUpdateBy)');
"@
}

$Script:Tables.city | ForEach-Object {
    @"
INSERT INTO U03vHM.city
	(cityId, city, countryId, createDate, createdBy, lastUpdate, lastUpdateBy)
	VALUES ($($_.cityId), '$($_.city)', $($_.countryId), '$($_.createDate.ToString('yyyy-MM-dd HH:mm:ss'))', '$($_.createdBy)', '$($_.lastUpdate.ToString('yyyy-MM-dd HH:mm:ss'))', '$($_.lastUpdateBy)');
"@
}

$Script:Tables.address | ForEach-Object {
    @"
INSERT INTO U03vHM.address
	(addressId, address, address2, cityId, postalCode, phone, createDate, createdBy, lastUpdate, lastUpdateBy)
	VALUES ($($_.addressId), '$($_.address)', '$($_.address2)', $($_.cityId), '$($_.postalCode)', '$($_.phone)', '$($_.createDate.ToString('yyyy-MM-dd HH:mm:ss'))', '$($_.createdBy)', '$($_.lastUpdate.ToString('yyyy-MM-dd HH:mm:ss'))', '$($_.lastUpdateBy)');
"@
}

$Script:Tables.customer.all | ForEach-Object {
    @"
INSERT INTO U03vHM.customer
	(customerId, customerName, addressId, active, createDate, createdBy, lastUpdate, lastUpdateBy)
	VALUES ($($_.customerId), '$($_.customerName)', $($_.addressId), $(if ($_.active) { 'TRUE' } else { 'FALSE' }), '$($_.createDate.ToString('yyyy-MM-dd HH:mm:ss'))', '$($_.createdBy)', '$($_.lastUpdate.ToString('yyyy-MM-dd HH:mm:ss'))', '$($_.lastUpdateBy)');
"@
}

$Script:Tables.appointment | ForEach-Object {
    @"
INSERT INTO U03vHM.appointment
	(appointmentId, customerId, userId, title, description, location, contact, type, url, start, end, createDate, createdBy, lastUpdate`,`lastUpdateBy)
	VALUES ($($_.appointmentId), $($_.customerId), $($_.userId), '$($_.title)', '$($_.description)', '$($_.location)', '$($_.contact)', '$($_.type)', '$($_.url)', '$($_.start.ToString('yyyy-MM-dd HH:mm:ss'))', '$($_.end.ToString('yyyy-MM-dd HH:mm:ss'))', '$($_.createDate.ToString('yyyy-MM-dd HH:mm:ss'))', '$($_.createdBy)', '$($_.lastUpdate.ToString('yyyy-MM-dd HH:mm:ss'))', '$($_.lastUpdateBy)');
"@
}
#>