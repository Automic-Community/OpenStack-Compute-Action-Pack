## Getting Started:


###### Description

OpenStack is a set of software tools for building and managing cloud computing platforms for public and private clouds. OpenStack is an open source software managed by the OpenStack Foundation a non-profit which oversees both development and community-building around the project. OpenStack lets users deploy virtual machines and other instances which handle different tasks for managing a cloud environment on the fly.

OpenStack is made up of many different moving parts. Because of its open nature, anyone can add additional components to OpenStack to help it to meet their needs. But the OpenStack community has collaboratively identified nine key components that are a part of the "core" of OpenStack, which are distributed as a part of any OpenStack system and officially maintained by the OpenStack community. 
		
###### Actions

1. Get Authentication Token Action
2. List Servers Action
3. Get Server Details Action
4. Create Servers Action
5. Change Server State Action
6. Create Server Snapshot Action
7. Check Server Status Action
8. List Snapshots Action
9. Delete Server Action
10. Assign/Remove Floating IP

###### Prerequisite:

1. Automation Engine should be installed.
2. Automic Package Manager should be installed.
3. ITPA Shared Action Pack should be installed.
4. Java 1.7 or higher
5. Maven

###### Steps to install action pack source code:

1. Clone the code to your machine.
2. Go to the pom.xml directory.(source/tools/)
       Example: **mvn clean package -DskipTests**
    
3. Run the command apm upload in the directory which contains package.yml (source/):
Ex. **apm upload -force -u <Name>/<Department> -c <Client-id> -H <Host> -pw <Password> -S AUTOMIC -y -ia -ru**


###### Package/Action Documentation

Please refer to the link for [package documentation](source/ae/DOCUMENTATION/PCK.AUTOMIC_CA_APM.PUB.DOC.xml)

###### Third party licenses:

The third-party library and license document reference.[Third party licenses](source/ae/DOCUMENTATION/PCK.AUTOMIC_CA_APM.PUB.LICENSES.xml)

###### Useful References

1. [About Packs and Plug-ins](https://docs.automic.com/documentation/webhelp/english/AA/12.3/DOCU/12.3/Automic%20Automation%20Guides/help.htm#PluginManager/PM_AboutPacksandPlugins.htm?Highlight=Action%20packs)
2. [Working with Packs and Plug-ins](https://docs.automic.com/documentation/webhelp/english/AA/12.3/DOCU/12.3/Automic%20Automation%20Guides/help.htm#PluginManager/PM_WorkingWith.htm#link10)
3. [Actions and Action Packs](https://docs.automic.com/documentation/webhelp/english/AA/12.3/DOCU/12.3/Automic%20Automation%20Guides/help.htm#_Common/ReleaseHighlights/RH_Plugin_PackageManager.htm?Highlight=Action%20packs)
4. [PACKS Compatibility Mode](https://docs.automic.com/documentation/webhelp/english/AA/12.3/DOCU/12.3/Automic%20Automation%20Guides/help.htm#AWA/Variables/UC_CLIENT_SETTINGS/UC_CLIENT_PACKS_COMPATIBILITY_MODE.htm?Highlight=Action%20packs)
5. [Working with actions](https://docs.automic.com/documentation/webhelp/english/AA/12.3/DOCU/12.3/Automic%20Automation%20Guides/help.htm#ActionBuilder/AB_WorkingWith.htm#link4)
6. [Installing and Configuring the Action Builder](https://docs.automic.com/documentation/webhelp/english/AA/12.3/DOCU/12.3/Automic%20Automation%20Guides/help.htm#ActionBuilder/install_configure_plugins_AB.htm?Highlight=Action%20packs)

###### Distribution: 

In the distribution process, we can download the existing or updated action package from the Automation Engine by using the apm build command.
Example: **apm build -y -H AE_HOST -c 106 -u TEST/TEST -pw password -d /directory/ -o zip -v action_pack_name**
			
			
###### Copyright and License: 

Broadcom does not support, maintain or warrant Solutions, Templates, Actions and any other content published on the Community and is subject to Broadcom Community [Terms and Conditions](https://community.broadcom.com/termsandconditions)
