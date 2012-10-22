; -- Example1.iss --
; Demonstrates copying 3 files and creating an icon.

; SEE THE DOCUMENTATION FOR DETAILS ON CREATING .ISS SCRIPT FILES!

[Setup]
AppName=OurGrid Broker
AppVersion=4.3.0
AppPublisher=OurGrid
AppCopyright=OurGrid
DefaultDirName={pf}\OurGrid\Broker
DefaultGroupName=OurGrid
UninstallDisplayIcon={app}\broker.bat
Compression=lzma2
SolidCompression=yes
OutputDir=userdocs:.ourgrid\Broker\setup

[Files]
Source: "..\ReplaceVariables.vbs"; DestDir: {src}
Source: "..\ReplaceTokens.vbs"; DestDir: {src}
Source: "..\GenerateCert.bat"; DestDir: {src}
Source: "broker.bat"; DestDir: "{app}"
Source: "log4j.cfg.xml"; DestDir: "{app}";
Source: "..\icon.ico"; DestDir: "{app}"
Source: "examples\*"; DestDir: "{app}\examples"; Flags: recursesubdirs; Permissions: everyone-modify
Source: "..\lib\*"; DestDir: "{app}\lib"; Flags: recursesubdirs
Source: "broker.properties"; DestDir: "{code:GetUserHome}\.broker\"; Flags: onlyifdoesntexist uninsneveruninstall

[Icons]
Name: "{group}\Broker\Broker"; Filename: "{app}\broker.bat"; IconFilename: "{app}\icon.ico"
Name: "{group}\Broker\Configure"; Filename: "notepad.exe"; Parameters: "{code:GetUserHome}\.broker\broker.properties";
Name: "{group}\Broker\Uninstall"; Filename: "{uninstallexe}";

[Run]
Filename: {src}\GenerateCert.bat; Parameters: """{code:GetUserHome}\.broker\broker.properties"" ""{code:GetXMPPUser}"" ""{code:GetXMPPServer}"" ""{code:Normalize|{code:GetUserHome}\.broker\certification\mycertificate\mycertificate.cer}"" ""{app}\lib"""; Flags: skipifdoesntexist waituntilterminated runhidden shellexec; StatusMsg: Setting configuration
Filename: {src}\ReplaceVariables.vbs; Parameters: """{code:GetUserHome}\.broker\broker.properties"" ""commune.xmpp.username={code:GetXMPPUser};commune.xmpp.servername={code:GetXMPPServer};commune.xmpp.password={code:GetXMPPPassword};broker.peer.address={code:GetPeerUser}@{code:GetPeerServer}"" ""="" "; Flags: skipifdoesntexist waituntilterminated runhidden shellexec; StatusMsg: Setting configuration
Filename: {src}\ReplaceTokens.vbs; Parameters: " ""{app}\broker.bat"" ""[[LOG4J]]={code:Normalize|{app}\log4j.cfg.xml}"" "; Flags: skipifdoesntexist waituntilterminated runhidden shellexec; StatusMsg: Setting configuration
Filename: {src}\ReplaceTokens.vbs; Parameters: " ""{app}\log4j.cfg.xml"" ""[[LOGDIR]]={code:Normalize|{code:GetUserHome}\.broker\logs\log}"" "; Flags: skipifdoesntexist waituntilterminated runhidden shellexec; StatusMsg: Setting configuration

[UninstallDelete]
Type: dirifempty; Name: {pf}\OurGrid\Broker; 
Type: dirifempty; Name: {pf}\OurGrid;

[Code]
var
  UserPage: TInputQueryWizardPage;
  PeerPage: TInputQueryWizardPage;
    
procedure InitializeWizard;
begin
  { Create the pages }

  UserPage := CreateInputQueryPage(wpWelcome,
    'XMPP Information', 'To what XMPP server will the Broker connect?',
    'Please specify the XMPP username and the XMPP servername for the OurGrid Broker, then click Next.');
  UserPage.Add('XMPP username:', False);
  UserPage.Add('XMPP servername:', False);
  UserPage.Add('XMPP password:', True);

  PeerPage := CreateInputQueryPage(UserPage.ID,
    'OurGrid Peer Information', 'To what Peer will the Broker connect?',
    'Please specify the XMPP username and the XMPP servername of the OurGrid Peer this Broker will connect, then click Next.' + 
    'Remember that the Broker must be registered at the Peer.');
  PeerPage.Add('Peer username:', False);
  PeerPage.Add('Peer servername:', False);

  
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
  S := S + 'Peer address:' + NewLine
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

function Normalize(Param: String): String;
begin
  StringChange(Param, '\', '\\');
  Result := Param
end;

function GetUserHome(Param: String): String;
begin
  Result := GetEnv('HOMEDRIVE') + GetEnv('HOMEPATH');
end;
