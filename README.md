# XPages and Beer

Website and CMS for the [XPages &amp; Beer](http://www.xpagesandbeer.nl/) event.

# Getting started

The project consists of two parts, the Homepage (home.nsf) and the CMS (cms.nsf)

To get this project running you need to take the following steps:

* Clone this GitHub repository
* Import both projects into Domino Designer
* Create NSF's for both projects.
* Configure CMS location in Homepage
* Add Role to the ACL in cms.nsf

## Clone Repo

Checkout [this repository](https://github.com/thimo/xpagesandbeer.git)
[(or your own fork)](https://github.com/thimo/xpagesandbeer/fork)
via Command Line or a tool like [SourceTree](http://www.sourcetreeapp.com/):

    git clone https://github.com/thimo/xpagesandbeer.git

## Import

Import the on-disk project in Domino Designer:

* Window -> Show Perspective Views -> Package Explorer
* Right-Click in the Package Explorer -> Import
* General -> Existing Projects into Workspace
* Browse to the location where you checked out the code
* Select the projects -> Finish

## Create NSF's

Create new NSF for the on-disk projects:

* Right-Click the newly imported project
* Team Development -> Associate with new NSF
* Enter the server and name for the new NSF and click Finish.

## Configure Homepage

Configure the CMS location in the Homepage.

* Open the project 'XPages & Beer' in Domino Designer
* Open the file Resources/Files/settings.properties
* Update the value of 'cms_db' with the path on the server to cms.nsf.

## Add role

Add the *administrator* role and assign it to your user.

* Open the project 'XPages & Beer CMS' in Domino Designer
* Right-Click -> Application -> Access Control
* Tab: Roles -> Add -> *administrator* -> OK
* Tab: Basic -> Select your user -> Enable newly added role


# That's it!

The Homepage and CMS should now work. Happy hacking!
