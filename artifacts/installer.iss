;----------------------------------------------------------------------------------------
; Steganography utility to hide messages into cover files
; Copyright (c) Samir Vaidya (mailto:syvaidya@gmail.com)
;----------------------------------------------------------------------------------------

[Setup]
AppName=@AppName@
AppVersion=@AppVersion@
AppVerName=@AppName@ v@AppVersion@
AppPublisher=@AuthorName@
AppPublisherURL=@HomepageUrl@
WizardStyle=modern
SourceDir=@AppDir@
OutputDir=@DistDir@
DefaultDirName={autopf}\@AppName@
DefaultGroupName=@AppName@
OutputBaseFilename=Setup-@AppName@-@AppVersion@
LicenseFile=LICENSE
UninstallDisplayIcon={app}\DIRP.ico
PrivilegesRequired=lowest
PrivilegesRequiredOverridesAllowed=dialog

[Files]
Source: "DIRP.bat"; DestDir: "{app}"
Source: "DIRP.ico"; DestDir: "{app}"
Source: "README"; DestDir: "{app}"
Source: "LICENSE"; DestDir: "{app}"
Source: "lib\*"; DestDir: "{app}\lib"

[Icons]
Name: "{group}\@AppName@"; Filename: "{app}\DIRP.bat"; WorkingDir: "{app}"; IconFilename: "{app}\DIRP.ico"
