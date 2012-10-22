; -- Example1.iss --
; Demonstrates copying 3 files and creating an icon.

; SEE THE DOCUMENTATION FOR DETAILS ON CREATING .ISS SCRIPT FILES!

[Setup]
AppName=OurGrid Peer
AppVersion=4.3.0
AppPublisher=OurGrid
AppCopyright=OurGrid
DefaultDirName={pf}\OurGrid\Peer
DefaultGroupName=OurGrid
UninstallDisplayIcon={app}\peer.bat
Compression=lzma2
SolidCompression=yes
OutputDir=userdocs:.ourgrid\Peer\setup

[Files]
Source: "..\ReplaceVariables.vbs"; DestDir: "{src}"
Source: "..\ReplaceTokens.vbs"; DestDir: "{src}"
Source: "..\GenerateCert.bat"; DestDir: "{src}"
Source: "peer.properties"; DestDir: "{userappdata}\OurGrid\Peer"; Flags: onlyifdoesntexist uninsneveruninstall
Source: "log4j.cfg.xml"; DestDir: "{app}"; Flags: onlyifdoesntexist
Source: "peer-hibernate.cfg.xml"; DestDir: "{app}";
Source: "peer.bat"; DestDir: "{app}"
Source: "peer-nogui.bat"; DestDir: "{app}"
Source: "..\icon.ico"; DestDir: "{app}"
Source: "..\lib\*"; DestDir: "{app}\lib"; Flags: recursesubdirs

[Icons]
Name: "{group}\Peer\Peer"; Filename: "{app}\peer.bat"; IconFilename: "{app}\icon.ico"
Name: "{group}\Peer\Configure"; Filename: "notepad.exe"; Parameters: "{userappdata}\OurGrid\Peer\peer.properties";
Name: "{group}\Peer\Uninstall"; Filename: "{uninstallexe}";

[Run]
Filename: {src}\GenerateCert.bat; Parameters: """{userappdata}\OurGrid\Peer\peer.properties"" ""{code:GetXMPPUser}"" ""{code:GetXMPPServer}"" ""{code:Normalize|{userappdata}\OurGrid\Peer\certification\mycertificate\mycertificate.cer}"" ""{app}\lib"""; Flags: skipifdoesntexist waituntilterminated runhidden shellexec; StatusMsg: Setting configuration
Filename: {src}\ReplaceVariables.vbs; Parameters: """{userappdata}\OurGrid\Peer\peer.properties"" ""commune.xmpp.username={code:GetXMPPUser};commune.xmpp.servername={code:GetXMPPServer};commune.xmpp.password={code:GetXMPPPassword};peer.ds.network={code:GetDSUser}@{code:GetDSServer}"" ""="" "; Flags: skipifdoesntexist waituntilterminated runhidden shellexec; StatusMsg: Setting configuration
Filename: {src}\ReplaceTokens.vbs; Parameters: " ""{app}\peer.bat"" ""[[OGROOT]]={code:Normalize|{userappdata}\OurGrid\Peer};[[LOG4J]]={code:Normalize|{app}\log4j.cfg.xml}"" "; Flags: skipifdoesntexist waituntilterminated runhidden shellexec; StatusMsg: Setting configuration
Filename: {src}\ReplaceTokens.vbs; Parameters: " ""{app}\peer-nogui.bat"" ""[[OGROOT]]={code:Normalize|{userappdata}\OurGrid\Peer};[[LOG4J]]={code:Normalize|{app}\log4j.cfg.xml}"" "; Flags: skipifdoesntexist waituntilterminated runhidden shellexec; StatusMsg: Setting configuration
Filename: {src}\ReplaceTokens.vbs; Parameters: " ""{app}\log4j.cfg.xml"" ""[[LOGDIR]]={code:Normalize|{userappdata}\OurGrid\Peer\logs\log}"" "; Flags: skipifdoesntexist waituntilterminated shellexec; StatusMsg: Setting configuration
Filename: {src}\ReplaceTokens.vbs; Parameters: " ""{app}\peer-hibernate.cfg.xml"" ""[[OGROOT]]={code:Normalize|{userappdata}\OurGrid\Peer}"" "; Flags: skipifdoesntexist waituntilterminated shellexec; StatusMsg: Setting configuration


[Registry]
Root: HKLM; Subkey: Software\Microsoft\Windows\CurrentVersion\Run; ValueType: string; Check: InstallService; Flags: uninsdeletekeyifempty uninsdeletevalue createvalueifdoesntexist; ValueName: OurGrid Peer; ValueData: "start /min cmd /c ""{app}\peer-nogui.bat"" "

[UninstallDelete]
Type: dirifempty; Name: {pf}\OurGrid\Peer; 
Type: dirifempty; Name: {pf}\OurGrid;

[Code]
var
  UserPage: TInputQueryWizardPage;
  DSPage: TInputQueryWizardPage;
  ServicePage: TInputOptionWizardPage;
    
procedure InitializeWizard;
begin
  { Create the pages }

  UserPage := CreateInputQueryPage(wpWelcome,
    'XMPP Information', 'To what XMPP server will the Peer connect?',
    'Please specify the XMPP information for the OurGrid Peer, then click Next.');
  UserPage.Add('XMPP username:', False);
  UserPage.Add('XMPP servername:', False);
  UserPage.Add('XMPP password:', True);

  DSPage := CreateInputQueryPage(UserPage.ID,
    'OurGrid Discovery Service Information', 'To what Discovery Service will this Peer connect?',
    'Please specify the XMPP username and the XMPP servername of the primary OurGrid Discovery Service this Peer will connect, then click Next.');
  DSPage.Add('Discovery Service username:', False);
  DSPage.Add('Discovery Service servername:', False);

  ServicePage := CreateInputOptionPage(DSPage.ID,
    'Windows logon', 'Start with Windows',
    'Please check if you want the OurGrid Peer to start with Windows, then click Next.',
    False, False);
  ServicePage.Add('Start OurGrid Peer on Windows boot?');
  ServicePage.Values[0] := True;
  
  { Set default values, using settings that were stored last time if possible }

  UserPage.Values[0] := GetPreviousData('XMPPUser',  Lowercase(ExpandConstant('{sysuserinfoname}')));
  UserPage.Values[1] := GetPreviousData('XMPPServer', 'xmpp.ourgrid.org');
  UserPage.Values[2] := GetPreviousData('XMPPPassword', 'xmpp-password');
  DSPage.Values[0] := GetPreviousData('DSUser', 'lsd-ds');
  DSPage.Values[1] := GetPreviousData('DSServer', 'xmpp.ourgrid.org');

end;

procedure RegisterPreviousData(PreviousDataKey: Integer);
begin
  { Store the settings so we can restore them next time }
  SetPreviousData(PreviousDataKey, 'XMPPUser', UserPage.Values[0]);
  SetPreviousData(PreviousDataKey, 'XMPPServer', UserPage.Values[1]);
  SetPreviousData(PreviousDataKey, 'XMPPPassword', UserPage.Values[2]);
  SetPreviousData(PreviousDataKey, 'DSUser', DSPage.Values[0]);
  SetPreviousData(PreviousDataKey, 'DSServer', DSPage.Values[1]);
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

  if CurPageID = DSPage.ID then begin
    if (DSPage.Values[0] = '') OR (DSPage.Values[1] = '') then begin
      MsgBox('You must enter the Discovery Service information.', mbError, MB_OK);
      Result := False;
    end;
  end

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
  S := S + 'Discovery Service address:' + NewLine
  S := S + Space + DSPage.Values[0] + '@'  + DSPage.Values[1] + NewLine;
  
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

function GetDSUser(Param: String): String;
begin
  Result := DSPage.Values[0];
end;

function GetDSServer(Param: String): String;
begin
  Result := DSPage.Values[1];
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