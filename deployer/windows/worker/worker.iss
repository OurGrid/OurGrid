; -- Example1.iss --
; Demonstrates copying 3 files and creating an icon.

; SEE THE DOCUMENTATION FOR DETAILS ON CREATING .ISS SCRIPT FILES!

[Setup]
AppName=OurGrid Worker
AppVersion=4.3.0
AppPublisher=OurGrid
AppCopyright=OurGrid
DefaultDirName={pf}\OurGrid\Worker
DefaultGroupName=OurGrid
UninstallDisplayIcon={app}\worker.bat
Compression=lzma2
SolidCompression=yes
OutputDir=userdocs:.ourgrid\Worker\setup

[Files]
Source: "..\ReplaceVariables.vbs"; DestDir: "{src}"
Source: "..\ReplaceTokens.vbs"; DestDir: "{src}"
Source: "..\GenerateCert.bat"; DestDir: "{src}"
Source: "..\wget\*"; DestDir: "{src}\wget"
Source: "worker.properties"; DestDir: "{userappdata}\OurGrid\Worker"; Flags: onlyifdoesntexist uninsneveruninstall
Source: "log4j.cfg.xml"; DestDir: "{app}"; Flags: onlyifdoesntexist
Source: "worker.bat"; DestDir: "{app}"
Source: "worker-nogui.bat"; DestDir: "{app}"
Source: "..\icon.ico"; DestDir: "{app}"
Source: "..\lib\*"; DestDir: "{app}\lib"; Flags: recursesubdirs
Source: "..\lib\ourvirt*"; DestDir: "{app}\commons"; Flags: uninsneveruninstall
Source: "..\lib\commons*"; DestDir: "{app}\commons"; Flags: uninsneveruninstall
Source: "..\lib\gson*"; DestDir: "{app}\commons"; Flags: uninsneveruninstall
Source: "uninstall.bat"; DestDir: "{app}"; Flags: uninsneveruninstall
Source: "remove-vm.bat"; DestDir: "{app}"; Flags: uninsneveruninstall

[Dirs]
Name: "{app}\.storage"
Name: "{app}\playpen"

[Icons]
Name: "{group}\Worker\Worker"; Filename: "{app}\worker.bat"; IconFilename: "{app}\icon.ico"
Name: "{group}\Worker\Configure"; Filename: "notepad.exe"; Parameters: "{userappdata}\OurGrid\Worker\worker.properties";
Name: "{group}\Worker\Uninstall"; Filename: "{app}\uninstall.bat"; IconFilename: "{uninstallexe}" 

[Run]
Filename: {src}\GenerateCert.bat; Parameters: """{userappdata}\OurGrid\Worker\worker.properties"" ""{code:GetXMPPUser}"" ""{code:GetXMPPServer}"" ""{code:Normalize|{userappdata}\OurGrid\Worker\certification\mycertificate\mycertificate.cer}"" ""{app}\lib"""; Flags: skipifdoesntexist waituntilterminated runhidden shellexec; StatusMsg: Setting configuration
Filename: {src}\ReplaceVariables.vbs; Parameters: """{userappdata}\OurGrid\Worker\worker.properties"" ""commune.xmpp.username={code:GetXMPPUser};commune.xmpp.servername={code:GetXMPPServer};commune.xmpp.password={code:GetXMPPPassword};worker.peer.address={code:GetPeerUser}@{code:GetPeerServer};worker.storagedir={code:Normalize|{userappdata}\OurGrid\Worker\.storage};worker.playpenroot={code:Normalize|{userappdata}\OurGrid\Worker\playpen}"" ""="" "; Flags: skipifdoesntexist waituntilterminated shellexec; StatusMsg: Setting configuration
Filename: {src}\ReplaceTokens.vbs; Parameters: " ""{app}\worker.bat"" ""[[OGROOT]]={code:Normalize|{userappdata}\OurGrid\Worker};[[LOG4J]]={code:Normalize|{app}\log4j.cfg.xml}"" "; Flags: skipifdoesntexist waituntilterminated shellexec; StatusMsg: Setting configuration
Filename: {src}\ReplaceTokens.vbs; Parameters: " ""{app}\worker-nogui.bat"" ""[[OGROOT]]={code:Normalize|{userappdata}\OurGrid\Worker};[[LOG4J]]={code:Normalize|{app}\log4j.cfg.xml}"" "; Flags: skipifdoesntexist waituntilterminated shellexec; StatusMsg: Setting configuration
Filename: {src}\ReplaceTokens.vbs; Parameters: " ""{app}\log4j.cfg.xml"" ""[[LOGDIR]]={code:Normalize|{userappdata}\OurGrid\Worker\logs\log}"" "; Flags: skipifdoesntexist waituntilterminated shellexec; StatusMsg: Setting configuration
Filename: {src}\ReplaceTokens.vbs; Parameters: " ""{app}\remove-vm.bat"" ""[[COMMONS]]={app}\commons;[[VMNAME]]={code:GetVMName}"" "; Flags: skipifdoesntexist waituntilterminated shellexec; StatusMsg: Setting configuration
Filename: {src}\ReplaceTokens.vbs; Parameters: " ""{app}\uninstall.bat"" ""[[UNINSTALLEXE]]={code:Normalize|{uninstallexe}} "" "; Flags: skipifdoesntexist waituntilterminated shellexec; StatusMsg: Setting configuration

; Default Sandboxing
Filename: {src}\ReplaceVariables.vbs; Parameters: """{userappdata}\OurGrid\Worker\worker.properties"" ""worker.executor=GENERIC;vm.disk.path={code:Normalize|{userappdata}\OurGrid\Worker\vm-image.vdi}"" ""="" "; Flags: skipifdoesntexist waituntilterminated shellexec; StatusMsg: Setting up Virtual Box; Check: IsVBoxAndDefaultSandbox
Filename: {src}\wget\wget.exe; Parameters: " -N -O ""{userappdata}\OurGrid\Worker\vm-image.vdi"" ""http://maven.ourgrid.org/repos/linux/vbox/linux-vbox/og-image.vdi"" "; Flags: skipifdoesntexist waituntilterminated shellexec; StatusMsg: Downloading Virtual Box image; Check: IsVBoxAndDefaultSandbox

; Custom Sandboxing
Filename: {src}\ReplaceVariables.vbs; Parameters: """{userappdata}\OurGrid\Worker\worker.properties"" ""worker.executor=GENERIC;vm.disk.path={code:Normalize|{code:GetDiskImagePath}};vm.name={code:GetVMName};vm.user={code:GetVMUser};vm.password={code:GetVMPassword};vm.disk.type={code:GetVMDiskType};vm.os={code:GetOSType};vm.os.version={code:GetOSVersion};vm.memory={code:GetVMMem}"" ""="" "; Flags: skipifdoesntexist waituntilterminated shellexec; StatusMsg: Setting up Virtual Box; Check: IsVBoxAndCustomSandbox

[UninstallDelete]
Type: dirifempty; Name: {pf}\OurGrid\Worker; 
Type: dirifempty; Name: {pf}\OurGrid;


[Registry]

Root: HKLM; Subkey: Software\Microsoft\Windows\CurrentVersion\Run; ValueType: string; Check: InstallService; Flags: uninsdeletekeyifempty uninsdeletevalue createvalueifdoesntexist; ValueName: OurGrid Worker; ValueData: "start /min cmd /c ""{app}\worker-nogui.bat"" "

[Code]
var
  UserPage: TInputQueryWizardPage;
  PeerPage: TInputQueryWizardPage;
  FlavorPage: TInputOptionWizardPage;
  VBoxPage: TInputOptionWizardPage;
  VBoxDiskPage: TInputFileWizardPage;
  VBoxSOPage: TInputQueryWizardPage;
  VBoxLoginPage: TInputQueryWizardPage;
  ServicePage: TInputOptionWizardPage;
  ComboOSType: TComboBox;
  VMDiskType: TComboBox;
  OSVersionEdit: TEdit;
  VBoxNamePage: TInputQueryWizardPage;
  Virtualization, COnfiguration, VMName : String;

procedure InitializeWizard;
var
  OSVersionLabel, OSTypeLabel, VMDiskTypeLabel: TLabel;
begin
  
  VMName := 'owvbox_1';
  Virtualization := 'None';
  Configuration := 'None';
    
  { Create the pages }
  UserPage := CreateInputQueryPage(wpWelcome,
    'XMPP Information', 'To what XMPP server will the Worker connect?',
    'Please specify the XMPP information for the OurGrid Worker, then click Next.');
  UserPage.Add('XMPP username:', False);
  UserPage.Add('XMPP servername:', False);
  UserPage.Add('XMPP password:', True);

  PeerPage := CreateInputQueryPage(UserPage.ID,
    'OurGrid Discovery Service Information', 'To what Peer will this Worker connect?',
    'Please specify the XMPP username and the XMPP servername of the OurGrid Peer this Worker will connect, then click Next. ' + 
    'Notice that this Worker must have a certificate issued by the OurGrid Peer it will connect to.');
  PeerPage.Add('Peer username:', False);
  PeerPage.Add('Peer servername:', False);

  FlavorPage := CreateInputOptionPage(PeerPage.ID,
    'Personal Information', 'How will you use the OurGrid Worker?',
    'Please specify what kind of Virtualization strategy do you want to use, then click Next.',
    True, False);
  FlavorPage.Add('Vanilla (no Sandboxed Execution)');
  FlavorPage.Add('Virtual Box');
  FlavorPage.SelectedValueIndex := 0
    
  VBoxPage := CreateInputOptionPage(FlavorPage.ID,
    'Sandbox Information', 'Which Virtual Box configuration will you use?',
    'Please specify what Virtual Box machine configuration do you want to use, then click Next.',
    True, False);
  VBoxPage.Add('Default (Provided by the OurGrid Team)');
  VBoxPage.Add('Custom');
  VBoxPage.SelectedValueIndex := 0

  // VM Name and Ram
  VBoxNamePage := CreateInputQueryPage(VBoxPage.ID,
    'Virtual Box VM configuration', 'Which name you will give to the Virtual Machine? And how much RAM?',
    'Please specify the VM Name and RAM size, then click Next. ');
  VBoxNamePage.Add('VM Name:', False);
  VBoxNamePage.Add('VM Memory (in MB):', False);
  VBoxNamePage.Values[0] := VMName;
  VBoxNamePage.Values[1] := '128';

  // VM Disk
  VBoxDiskPage := CreateInputFilePage(VBoxNamePage.ID,
    'Select Disk Image location', 'Where is the Disk Image file located?',
    'Select where the Disk Image is located, then click Next. Notice that this image MUST have GuestAdditions installed.');

  VMDiskTypeLabel := TLabel.Create(VBoxDiskPage);    
  VMDiskTypeLabel.Parent := VBoxDiskPage.Surface;
  VMDiskTypeLabel.Caption := 'VM Disk Type:';
  VMDiskTypeLabel.Left := ScaleX(0); VMDiskTypeLabel.Top := ScaleY(90); VMDiskTypeLabel.Width := ScaleX(70); VMDiskTypeLabel.Height := ScaleY(13);

  VMDiskType := TComboBox.Create(VBoxDiskPage);
  VMDiskType.Width := VBoxDiskPage.SurfaceWidth;
  VMDiskType.Parent := VBoxDiskPage.Surface;
  VMDiskType.Style := csDropDownList;
  VMDiskType.Items.Add('SATA');
  VMDiskType.Items.Add('IDE');
  VMDiskType.Items.Add('SCSI');
  VMDiskType.Items.Add('SAS');
  VMDiskType.ItemIndex := 0
  VMDiskType.Left := ScaleX(0); VMDiskType.Top := ScaleY(106); VMDiskType.Width := ScaleX(70); VMDiskType.Height := ScaleY(13);

  VBoxDiskPage.Add('Location of the Disk Image:',         
    'Virtual Disk images|*.vdi|VMware Virtual Machine Disks|*.vmdk|Microsoft Virtual PC VHD|*.vhd|All files|*.*', '*.*');

  // VM SO
  VBoxSOPage := CreateInputQueryPage(VBoxDiskPage.ID,
    'Virtual Box VM configuration', 'Which OS will you use? ',
    'Please specify the Virtual Machine OS settings, then click Next. ');
  
  OSTypeLabel := TLabel.Create(VBoxSOPage);    
  OSTypeLabel.Parent := VBoxSOPage.Surface;
  OSTypeLabel.Caption := 'VM OS Type:';
  OSTypeLabel.Left := ScaleX(0); OSTypeLabel.Top := ScaleY(24); OSTypeLabel.Width := ScaleX(150); OSTypeLabel.Height := ScaleY(13);
  
  ComboOSType := TComboBox.Create(VBoxSOPage);
  ComboOSType.Width := VBoxSOPage.SurfaceWidth;
  ComboOSType.Parent := VBoxSOPage.Surface;
  ComboOSType.Style := csDropDownList;
  ComboOSType.Items.Add('Microsoft Windows');
  ComboOSType.Items.Add('Linux');
  ComboOSType.Items.Add('Solaris');
  ComboOSType.Items.Add('BSD');
  ComboOSType.Items.Add('IBM OS/2');
  ComboOSType.Items.Add('Mac OS X');
  ComboOSType.Items.Add('Other');
  ComboOSType.ItemIndex := 1
  ComboOSType.Left := ScaleX(0); ComboOSType.Top := ScaleY(40); ComboOSType.Width := ScaleX(150); ComboOSType.Height := ScaleY(13);

  OSVersionLabel := TLabel.Create(VBoxSOPage);    
  OSVersionLabel.Parent := VBoxSOPage.Surface;
  OSVersionLabel.Caption := 'VM OS Version (Eg.: Ubuntu):';
  OSVersionLabel.Left := ScaleX(0); OSVersionLabel.Top := ScaleY(80); OSVersionLabel.Width := ScaleX(150); OSVersionLabel.Height := ScaleY(13);

  OSVersionEdit := TEdit.Create(VBoxSOPage);    
  OSVersionEdit.Parent := VBoxSOPage.Surface;
  OSVersionEdit.Top := ScaleY(96); OSVersionEdit.Width := VBoxSOPage.SurfaceWidth;
  OSVersionEdit.Text := 'Ubuntu'

  // VM Login
  VBoxLoginPage := CreateInputQueryPage(VBoxSOPage.ID,
    'Virtual Box VM configuration', 'Which User will you use inside the Virtual Machine?',
    'Please specify the User settings, then click Next. ');
  VBoxLoginPage.Add('VM User name:', False);
  VBoxLoginPage.Add('VM User password:', True);
  VBoxLoginPage.Values[0] := 'worker';
  VBoxLoginPage.Values[1] := 'worker';

  // Service
  ServicePage := CreateInputOptionPage(VBoxLoginPage.ID,
    'Windows logon', 'Start with Windows',
    'Please check if you want the OurGrid Worker to start with Windows, then click Next.',
    False, False);
  ServicePage.Add('Start OurGrid Worker on Windows boot?');
  ServicePage.Values[0] := True;
  
  { Set default values, using settings that were stored last time if possible }

  UserPage.Values[0] := GetPreviousData('XMPPUser',  Lowercase(ExpandConstant('{sysuserinfoname}')));
  UserPage.Values[1] := GetPreviousData('XMPPServer', 'xmpp.ourgrid.org');
  UserPage.Values[2] := GetPreviousData('XMPPPassword', 'xmpp-password');
  PeerPage.Values[0] := GetPreviousData('PeerUser', 'lsd-peer');
  PeerPage.Values[1] := GetPreviousData('PeerServer', 'xmpp.ourgrid.org');

end;

procedure RegisterPreviousData(PreviousDataKey: Integer);
begin
  { Store the settings so we can restore them next time }
  SetPreviousData(PreviousDataKey, 'XMPPUser', UserPage.Values[0]);
  SetPreviousData(PreviousDataKey, 'XMPPServer', UserPage.Values[1]);
  SetPreviousData(PreviousDataKey, 'XMPPPassword', UserPage.Values[2]);
  SetPreviousData(PreviousDataKey, 'PeerUser', PeerPage.Values[0]);
  SetPreviousData(PreviousDataKey, 'PeerServer', PeerPage.Values[1]);
end;

function NextButtonClick(CurPageID: Integer): Boolean;
begin
  { Validate certain pages before allowing the user to proceed }
  if CurPageID = UserPage.ID then begin
    if (UserPage.Values[0] = '') OR (UserPage.Values[1] = '') OR (UserPage.Values[2] = '') then begin
      MsgBox('You must enter the XMPP information.', mbError, MB_OK);
      Result := False;
    end;
  end

  if CurPageID = PeerPage.ID then begin
    if (PeerPage.Values[0] = '') OR (PeerPage.Values[1] = '') then begin
      MsgBox('You must enter the Peer information.', mbError, MB_OK);
      Result := False;
    end;
  end

  if CurPageID = VBoxNamePage.ID then begin
    VMName := VBoxNamePage.Values[0];
  end

  if CurPageID = FlavorPage.ID then begin
    case FlavorPage.SelectedValueIndex of
      0: Virtualization := 'Vanilla';
      1: Virtualization := 'VBox';
    end;
  end

  if CurPageID = FlavorPage.ID then begin
    case FlavorPage.SelectedValueIndex of
      0: Virtualization := 'Vanilla';
      1: Virtualization := 'VBox';
    end;
  end

  if CurPageID = VBoxPage.ID then begin
    case VBoxPage.SelectedValueIndex of
      0: Configuration := 'Default';
      1: Configuration := 'Custom';
    end;
  end;

  Result := True;
end;

function UpdateReadyMemo(Space, NewLine, MemoUserInfoInfo, MemoDirInfo, MemoTypeInfo,
  MemoComponentsInfo, MemoGroupInfo, MemoTasksInfo: String): String;
var
  S: String;
begin
  { Fill the 'Ready Memo' with the normal settings and the custom settings }
  S := '';
  S := S + 'XMPP information:' + NewLine;
  S := S + Space + UserPage.Values[0] + '@'  + UserPage.Values[1] + NewLine;
  S := S + 'Peeer address:' + NewLine
  S := S + Space + PeerPage.Values[0] + '@'  + PeerPage.Values[1] + NewLine;
  
  Result := S;
end;

function GetXMPPUser(Param: String): String;
begin
  Result := UserPage.Values[0]
end;

function GetXMPPServer(Param: String): String;
begin
  Result := UserPage.Values[1];
end;

function GetXMPPPassword(Param: String): String;
begin
  Result := UserPage.Values[2];
end;

function GetPeerUser(Param: String): String;
begin
  Result := PeerPage.Values[0];
end;

function GetPeerServer(Param: String): String;
begin
  Result := PeerPage.Values[1];
end;

function GetDiskImagePath(Param: String): String;
begin
  Result := VBoxDiskPage.Values[0];
end;

function GetVMName(Param: String): String;
begin
  Result := VMName;
end;

function GetVMMem(Param: String): String;
begin
  Result := VBoxNamePage.Values[1];
end;

function GetVMUser(Param: String): String;
begin
  Result := VBoxLoginPage.Values[0];
end;

function GetVMPassword(Param: String): String;
begin
  Result := VBoxLoginPage.Values[1];
end;

function GetVMDiskType(Param: String): String;
begin
  case VMDiskType.ItemIndex of
      0: Result := 'SATA';
      1: Result := 'IDE';
      2: Result := 'SCSI';
      3: Result := 'SAS';
  end;
end;

function GetOSType(Param: String): String;
begin
  case ComboOSType.ItemIndex of
      0: Result := 'windows';
      1: Result := 'linux';
      2: Result := 'solaris';
      3: Result := 'bsd';
      4: Result := 'os2';
      5: Result := 'macos';
      6: Result := 'other';
  end;
end;

function RemoveGaps(Param: String): String;
begin
  StringChange(Param, ' ', '');
  StringChange(Param, '.', '');
  StringChange(Param, '\', '');
  Result := Param
end;

function GetOSVersion(Param: String): String;
begin
  Result := RemoveGaps(OSVersionEdit.Text);
end;

function IsVanilla(): Boolean;
begin
  if Virtualization = 'Vanilla' then
    Result := True
  else
    Result := False;
end;

function IsVBOX(): Boolean;
begin
  if Virtualization = 'VBox' then
    Result := True
  else
    Result := False;
end;

function IsDefaultSandbox(): Boolean;
begin
  if Configuration = 'Default' then
    Result := True
  else
    Result := False;
end;

function IsCustomSandbox(): Boolean;
begin
  if Configuration = 'Custom' then
    Result := True
  else
    Result := False;
end;

function InstallService(): Boolean;
begin
  Result := ServicePage.Values[0];
end;

function Normalize(Param: String): String;
begin
  StringChange(Param, '\', '\\');
  Result := Param
end;

function IsVBoxAndDefaultSandbox(): Boolean;
begin
  Result:= IsVBOX() and IsDefaultSandbox();
end;

function IsVBoxAndCustomSandbox(): Boolean;
begin
  Result:= IsVBOX() and IsCustomSandbox();
end;

function ShouldSkipPage(PageID: Integer): Boolean;
begin
  { Skip pages that shouldn't be shown }

  if (PageID = VBoxPage.ID) and (IsVanilla()) then
    Result := True
  else if (PageID = VBoxNamePage.ID) and (IsVanilla() or IsVBoxAndDefaultSandbox()) then
    Result := True
  else if (PageID = VBoxDiskPage.ID) and (IsVanilla() or IsVBoxAndDefaultSandbox()) then
    Result := True
  else if (PageID = VBoxSOPage.ID) and (IsVanilla() or IsVBoxAndDefaultSandbox()) then
    Result := True
  else if (PageID = VBoxLoginPage.ID) and (IsVanilla() or IsVBoxAndDefaultSandbox()) then
    Result := True
  else
    Result := False;
end;
