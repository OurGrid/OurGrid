Option Explicit
Dim objFSO,objFile,myFile,line,Result,outFile,objOutFile,tokens,TokenList,baseFile,sep

myFile=WScript.Arguments.Item(0)
tokens=WScript.Arguments.Item(1)
sep=WScript.Arguments.Item(2)

outFile=myFile & ".tmp"

TokenList = Split(tokens, ";")

Dim Size, El
For Each El in TokenList
	Size = Size + 1
Next

Dim UsedArray(), i
Redim Preserve UsedArray(Size)

For i = 0 to Size - 1
	UsedArray(i) = False
Next

Set objFSO = CreateObject("Scripting.FileSystemObject")

Set objFile = objFSO.OpenTextFile(myFile,1)
Set objOutFile = objFSO.OpenTextFile(outFile,2,True)

Dim SPair, Pair, Key, Value, FoundKey, SLine

Do Until objFile.AtEndOfLine
	line=objFile.ReadLine
	SLine = Split(line, sep)
		
	FoundKey = False 
	
	For i = 0 to Size - 1
		Pair = TokenList(i)		
		SPair = Split(Pair, "=")
		Key = SPair(0)
		Value = SPair(1)
		
		If Trim(SLine(0)) = Key Then
			objOutFile.WriteLine(Key & sep & Value)
			FoundKey = True
			UsedArray(i) = True
		End If
	Next
	
	If not FoundKey Then
		objOutFile.WriteLine(line)
	End If
Loop

For i = 0 to Size - 1
	If (not UsedArray(i)) Then
		Pair = TokenList(i)
		SPair = Split(Pair, "=")
		Key = SPair(0)
		Value = SPair(1)
		objOutFile.WriteLine(Key & sep & Value)
	End If
Next

Set objFile=Nothing
set objOutFile=Nothing

objFSO.DeleteFile myFile
objFSO.MoveFile outFile, myFile

Set objFSO=Nothing
WScript.Quit