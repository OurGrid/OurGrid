Option Explicit
Dim objFSO,objFile,myFile,line,Result,outFile,objOutFile,tokens,TokenList,baseFile,sep

Set objFSO = CreateObject("Scripting.FileSystemObject")

myFile=WScript.Arguments.Item(0)
tokens=WScript.Arguments.Item(1)

if not objFSO.FileExists(myFile) Then
	WScript.Quit
End If


outFile=myFile & ".tmp"
TokenList = Split(tokens, ";")

Set objFile = objFSO.OpenTextFile(myFile,1)
Set objOutFile = objFSO.OpenTextFile(outFile,2,True)

Dim SPair, Pair, Key, Value, i

Do Until objFile.AtEndOfLine
	line=objFile.ReadLine
		
	For Each Pair in TokenList
		SPair = Split(Pair, "=")
		Key = SPair(0)
		Value = SPair(1)
		
		line = Replace(line,Key,Value)
	Next
	
	objOutFile.WriteLine(line)
Loop

Set objFile=Nothing
set objOutFile=Nothing

objFSO.DeleteFile myFile
objFSO.MoveFile outFile, myFile

Set objFSO=Nothing
WScript.Quit