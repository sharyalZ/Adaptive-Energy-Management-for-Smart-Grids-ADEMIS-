# -*- coding: utf-8 -*-"""Created on Tue Jun  5 14:27:19 2018@author: jbblanc"""import sysfrom guppy import hpy#import numpy as npimport win32com.clientimport osimport gcimport getpassimport time#sys.path.append("C:\\Program Files\\DIgSILENT\\PowerFactory 2018\\Python\\3.6")#Definition of the path for PowerFactory methods#sys.path.append("C:\\Program Files\\DIgSILENT\\PowerFactory 2018\\Python\\3.6")#Definition of the path for PowerFactory methodssys.path.append("C:\\Program Files (x86)\\DIgSILENT\\PowerFactory 2018 SP7\\Python\\3.6")#Definition of the path for PowerFactory methodsimport powerfactoryfrom powerfactory import Applicationimport mathimport csv# Name for powerfactoryuser_name = getpass.getuser()#NAME="szafar"NAME=user_namesname='\\'+NAME+'.IntUser\\"'file_error=open('errors.txt','w')def print_error(message):    file_error.write(message) class PowerF:        def __init__(self):        self.k=1                print("Start InterfaceJavaPowerFactory in Python")        #print(str(hpy().heap()))        order=input()#order received from the java code:Init, Set, Solve,Get        #order="Init"        while(order!="End"):            try:                if(order=="Init"):                    self.Initialisation()                    print("end Init")############                elif(order=="Define Battery State"):                    self.DefineBatteryProfile()                    print("end Define Battery State")                elif(order=="DefineLoadProfile"):                    self.DefineLoadProfile()                    print("end load profile definition")                elif(order=="Sim"):                    self.SimulationComObject()                    print("end SimulationComObject")                elif(order=="ScriptSim"):                                    start1 = time.time()                    self.SimulationScript()                    print("end SimulationScript")                    end1 = time.time()                    print('Python total time:',end1 - start1 , file=file_error)                elif(order=="GetRes"):                    self.GetRes()                    print("end GetRes")                elif(order=="DefineBatteryState"):                    self.DefineBatteryState()                    print("end DefineBatteryState")                  elif(order=="GetEVs"):                    self.GetEVs()                    print("end GetEVs")                  elif(order=="GetBuses"):                    self.GetBuses()                    print("end GetBuses")                 elif(order=="GetLines"):                    self.GetLines()                    print("end GetLines")                 elif(order=="GetPVs"):                    self.GetPVs()                    print("end GetPVs")                  elif(order=="GetLoads"):                    self.GetLoads()                    print("end GetLoads")                  elif(order=="Get1Param"):                    self.Get1Param()                    print("end Get1Param")                  elif(order=="Set1Param"):                    self.Set1Param()                    print("end Set1Param")                 elif(order=="SetParam"):                    self.SetParam()                    print("end SetParam")                 elif(order=="Get2Param"):                    self.Get2Param()                    print("end Get2Param")                 elif(order=="GetComplete2Param"):                    self.GetComplete2Param()                    print("end GetComplete2Param")                 elif(order=="GetComplete1Param"):                    self.GetComplete1Param()                    print("end GetComplete1Param")                 elif(order=="GetSoE"):                    self.GetSoE()                    print("end GetSoE")                 elif(order=="GetPVPower"):                    self.GetPVPower()                    print("end GetPVPower")                 elif(order=="GetExternalGridPower"):                    self.GetExternalGridPower()                    print("end GetExternalGridPower")                 elif(order=="DefinePVProfile"):                    self.DefinePVProfile()                    print("end DefinePVProfile")                 elif(order=="EVConnection"):                    self.EVConnection()                    print("end EVConnection")                elif(order=="DefinePevs"):                    self.DefinePevs()                    print("end DefinePevs")                elif(order=="GetResultsFiles"):                    self.GetResultsFiles()                    print("end GetResultsFiles")                elif(order=="OpenPF"):                    self.OpenPF()                    print("end OpenPF")                 else:                    print("Unknown command : "+order)            #gc.collect()                            order=input()            except:                gc.collect()                print('error.code = %d' % error.code,file=file_error)    def Initialisation(self):        try:            self.ProjectName=input()            self.simulationPath=input()                        self.app = powerfactory.GetApplicationExt()            self.app.ActivateProject(self.ProjectName)             self.Project=self.app.GetActiveProject()            self.Project.Purge()            self.outputWindow=self.app.GetOutputWindow()            print(self.Project)                                            except powerfactory.ExitError as error:            print("error")            print(error)            print('error.code = %d' % error.code)                def DefineBatteryProfile(self):        try:            length=1200            M=[]            M.append([length,length])            for i in range(length):                M.append([i, 0.1*math.cos(2.0*3.141593*float(i)/50.0)])            P_DSL=self.Project.SearchObject(sname+self.ProjectName+r".IntPrj\Network Model.IntPrjfolder\Network Data.IntPrjfolder\Grid.ElmNet\Composite Model EV1.ElmComp\P-input EV1testPython.ElmDsl")            P_DSL.SetAttribute("matrix",M)        except powerfactory.ExitError as error:            print(error)            print('error.code = %d' % error.code)                            def DefineBatteryState(self):        P_InputBattery=input()        Power=input()        try:            #P_DSL=self.Project.SearchObject(r"\jbblanc.IntUser\SimpleGrid.IntPrj\Network Model.IntPrjfolder\Network Data.IntPrjfolder\Grid.ElmNet\Composite Model EV1.ElmComp\P-input EV1testPython.ElmDsl")            P_DSL=self.Project.SearchObject(P_InputBattery)            P_DSL.SetAttribute("params",[float(Power)])        except powerfactory.ExitError as error:            print(error)            print('error.code = %d' % error.code)                                    def Set1Param(self):        objectPath=input()        parameter=input()        index=input()        value=input()        try:            Object=self.Project.SearchObject(objectPath)            paramValue=Object.GetAttribute(parameter)            for i in range(len(paramValue)):                if(i==float(index)):                    paramValue[i]=float(value)            Object.SetAttribute(parameter,paramValue)        except powerfactory.ExitError as error:            print(error)            print('error.code = %d' % error.code)                def SetParam(self):        objectPath=input()        parameter=input()        value=input()        try:            Object=self.Project.SearchObject(objectPath)            Object.SetAttribute(parameter,int(value))        except powerfactory.ExitError as error:            print(error)            print('error.code = %d' % error.code)                            def DefineLoadProfile(self):        try:            FilePath=input()            period=float(input())            objectPath=input()            file_object  = open(FilePath, 'r')            M=[]            time=0            for line in file_object.readlines():                M.append([time,0.001*float(line)])                time+=period            P=[]            P.append([len(M),len(M)])            P.extend(M)            P_input=self.Project.SearchObject(objectPath)            P_input.SetAttribute("matrix",P)        except powerfactory.ExitError as error:            print(error)            print('error.code = %d' % error.code)                            def GetEVs(self):        try:            EVs=self.Project.GetContents("*.ElmGenstat",1)            for i in range(len(EVs)):                print(str(EVs[i])[4:-5])            print("End EVs")        except powerfactory.ExitError as error:            print(error)            print('error.code = %d' % error.code)                def GetBuses(self):        try:            Buses=self.Project.GetContents("*.ElmTerm",1)            for i in range(len(Buses)):                print(str(Buses[i])[4:-5])            print("End Buses")        except powerfactory.ExitError as error:            print(error)            print(len(Busses))            print('error.code = %d' % error.code)                def GetLines(self):        try:            Lines=self.Project.GetContents("*.ElmLne",1)            Transformers=self.Project.GetContents("*.ElmTr2",1) # change the file filter            Lines=Lines+Transformers            for i in range(len(Lines)):            	#print(str(Lines[i]),file=sys.stderr)                print(str(Lines[i])[4:-5])            print("End Lines")        except powerfactory.ExitError as error:            print(error)            print('error.code = %d' % error.code)                            """def GetModelLine(self):        try:            linePath=input()            line=self.Project.SearchObject(linePath)            #[error, R1, X1]=line.GetY1m(line)            M=np.eye(3,3)            res=line.GetZmatDist(50, 1, M)            print(res)            print(str(M[0,0]))            print(str(M[0,0]))        except powerfactory.ExitError as error:            print(error)            print('error.code = %d' % error.code) """                                                           def GetPVs(self):        try:            PVs=self.Project.GetContents("*.ElmPvsys",1)            for i in range(len(PVs)):                print(str(PVs[i])[4:-5])            print("End PVs")        except powerfactory.ExitError as error:            print(error)            print('error.code = %d' % error.code)                def GetLoads(self):        try:            Loads=self.Project.GetContents("*.ElmLod",1)            for i in range(len(Loads)):                print(str(Loads[i])[4:-5])            print("End Loads")        except powerfactory.ExitError as error:            print(error)            print('error.code = %d' % error.code)                def Get1Param(self):        try:            objectName=input()            param=input()            Object=self.Project.SearchObject(objectName)                        #print_error(objectName)            #print_error(Object)            #print(objectName,file=file_error)            #print(Object,file=file_error)            paramValue=Object.GetAttribute(param)            #print(paramValue,file=file_error)            print(str(paramValue)[4:-5])        except powerfactory.ExitError as error:            print(error)            print('error.code = %d' % error.code)                def GetComplete1Param(self):        try:            objectName=input()            param=input()            Object=self.Project.SearchObject(objectName)                        #print_error(objectName)            #print_error(Object)            #print(objectName,file=file_error)            #print(Object,file=file_error)            paramValue=Object.GetAttribute(param)            #print(paramValue,file=file_error)            print(str(paramValue))        except powerfactory.ExitError as error:            print(error)            print('error.code = %d' % error.code)                def Get2Param(self):        try:            objectName=input()            param=input()            Object=self.Project.SearchObject(objectName)            paramValue=Object.GetAttribute(param)            for i in range(len(paramValue)):                if((str(paramValue[i])[4:-5])!=""):                    print(str(paramValue[i])[4:-5])            print("End params")        except powerfactory.ExitError as error:            print(error)            print('error.code = %d' % error.code)                 def GetComplete2Param(self):        try:            objectName=input()            param=input()            Object=self.Project.SearchObject(objectName)            paramValue=Object.GetAttribute(param)            for i in range(len(paramValue)):                print(str(paramValue[i]))            print("End params")        except powerfactory.ExitError as error:            print(error)            print('error.code = %d' % error.code)                                                         def GetSoE(self):        try:            ComResName=sname+self.ProjectName+r".IntPrj\Study Cases.IntPrjfolder\Study Case.IntCase\Results\SoE.ComRes"            ComRes=self.Project.SearchObject(ComResName)            ComRes.Execute()            fileName=ComRes.GetAttribute("f_name")            print(str(fileName))        except powerfactory.ExitError as error:            print(error)            print('error.code = %d' % error.code)                               def GetPVPower(self):        try:            ComResName=sname+self.ProjectName+r".IntPrj\Study Cases.IntPrjfolder\Study Case.IntCase\Results\PVPower.ComRes"            ComRes=self.Project.SearchObject(ComResName)            ComRes.Execute()            fileName=ComRes.GetAttribute("f_name")            print(str(fileName))        except powerfactory.ExitError as error:            print(error)            print('error.code = %d' % error.code)         def GetExternalGridPower(self):        try:            ComResName=sname+self.ProjectName+r".IntPrj\Study Cases.IntPrjfolder\Study Case.IntCase\Results\ExternalGrid.ComRes"            ComRes=self.Project.SearchObject(ComResName)            ComRes.Execute()            fileName=ComRes.GetAttribute("f_name")            print(str(fileName))        except powerfactory.ExitError as error:            print(error)            print('error.code = %d' % error.code)                                                             def DefinePVProfile(self):        try:            objectPath=input()            file=input()            file_object  = open(file, 'r')            step=float(input())#Time step in second            Irr=[[0,0]]            i=0            for line in file_object.readlines():                #print(line)                values=line.split(',')                #print(values[4])                Irr.append([i*step+5*3600,0.001*float(values[4])])#To convert irradiance from W/m² to kW/m² for PF                i=i+1            Irr=[[len(Irr),len(Irr)]]  +Irr              ObjectPath=self.Project.SearchObject(objectPath)            ObjectPath.SetAttribute("matrix",Irr)        except powerfactory.ExitError as error:            print(error)            print('error.code = %d' % error.code)                            """def GetPower(self):        try:            objectName=input()            Object=self.Project.SearchObject(objectName)            print(Object)            power=Object.GetMeaValue()            print(power)        except powerfactory.ExitError as error:            print(error)            print('error.code = %d' % error.code)"""                 def EVConnection(self):        try:            self.app.ResetCalculation()            objectPath=input()            connection=input()#true to connect the EV, false to disconnect it            Object=self.Project.SearchObject(objectPath)            if(connection=="true"):                Object.Reconnect()            elif(connection=="false"):                Object.Disconnect()        except powerfactory.ExitError as error:            print(error)            print('error.code = %d' % error.code)               #############################################################            def DefinePevs(self):        try:            fileName=input()            scriptName=sname+self.ProjectName+r".IntPrj\Study Cases.IntPrjfolder\Study Case.IntCase\Scripts\DefinePevs.ComDpl"            script=self.Project.SearchObject(scriptName)            script.SetInputParameterString('fileName',fileName)            res=script.Execute()            print(res)        except powerfactory.ExitError as error:            print(error)            print('error.code = %d' % error.code)                #############################################################            def GetResultsFiles(self):        try:            SoEPath=sname+self.ProjectName+r".IntPrj\Study Cases.IntPrjfolder\Study Case.IntCase\Results\SoE.ComRes"            PVPath=sname+self.ProjectName+r".IntPrj\Study Cases.IntPrjfolder\Study Case.IntCase\Results\PVPower.ComRes"            ExternalGridPath=sname+self.ProjectName+r".IntPrj\Study Cases.IntPrjfolder\Study Case.IntCase\Results\ExternalGrid.ComRes"            VoltagePath=sname+self.ProjectName+r".IntPrj\Study Cases.IntPrjfolder\Study Case.IntCase\Results\Voltage.ComRes"            PLinePath=sname+self.ProjectName+r".IntPrj\Study Cases.IntPrjfolder\Study Case.IntCase\Results\PLine.ComRes"            ILinePath=sname+self.ProjectName+r".IntPrj\Study Cases.IntPrjfolder\Study Case.IntCase\Results\ILine.ComRes"            QLinePath=sname+self.ProjectName+r".IntPrj\Study Cases.IntPrjfolder\Study Case.IntCase\Results\QLine.ComRes"            PowerLoadPath=sname+self.ProjectName+r".IntPrj\Study Cases.IntPrjfolder\Study Case.IntCase\Results\LoadPower.ComRes"                        SoERes=self.Project.SearchObject(SoEPath)            PVRes=self.Project.SearchObject(PVPath)            ExternalGridRes=self.Project.SearchObject(ExternalGridPath)            VoltageRes=self.Project.SearchObject(VoltagePath)            PLineRes=self.Project.SearchObject(PLinePath)            ILineRes=self.Project.SearchObject(ILinePath)            QLineRes=self.Project.SearchObject(QLinePath)            PowerLoadRes=self.Project.SearchObject(PowerLoadPath)                        SoEFile=SoERes.GetAttribute("f_name")            PVFile=PVRes.GetAttribute("f_name")            ExternalGridFile=ExternalGridRes.GetAttribute("f_name")            VoltageFile=VoltageRes.GetAttribute("f_name")            PLineFile=PLineRes.GetAttribute("f_name")            ILineFile=ILineRes.GetAttribute("f_name")            QLineFile=QLineRes.GetAttribute("f_name")            PowerLoadFile=PowerLoadRes.GetAttribute("f_name")                        print(SoEFile)            print(PVFile)            print(ExternalGridFile)            print(VoltageFile)            print(VoltagePath)            print(PLineFile)            print(QLineFile)            print(ILineFile)            print(PowerLoadFile)        except powerfactory.ExitError as error:            print(error)            print('error.code = %d' % error.code)    #############################################################                        def OpenPF(self):        try:            self.app.Show()            Desk = self.app.GetFromStudyCase("SetDesktop")#Ldf or Sim            Desk.Show("Grid")        except powerfactory.ExitError as error:            print(error)            print('error.code = %d' % error.code)                         ###########################################################################################    Simulation with ComInc and ComSim object     ##################            ###############################################################################                def SimulationComObject(self):        Inc = self.app.GetFromStudyCase("ComInc")#Ldf or Sim        Sim = self.app.GetFromStudyCase("ComSim")#Ldf or Sim        stepSize=input()        tstart=input()        tstop=input()        Inc.SetAttribute("dtgrd",float(stepSize))        Inc.SetAttribute("tstart",float(tstart))        print("test")        print("result from initialisation : "+str(Inc.Execute()))        Sim.SetAttribute("tstop",float(tstop))        print("result from simulation : "+str(Sim.Execute()))          ###########################################################################################            Simulation with a script             ##################            ###############################################################################          def SimulationScript(self):        self.Project.Purge()        if self.k %10 ==0:            gc.collect()            self.k=1                self.k+=1        PevsFile=input()        SoE0File=input()        RatepvsFile=input()                stepSize=input()        tstart=input()        tstop=input()                try:                    print('PevsFile=',PevsFile,'SoE0File=',SoE0File,' RatepvsFile=',RatepvsFile, 'stepSize=',stepSize,' tstart=',tstart,' tstop=',tstop,file=file_error)                       #        scriptName=sname+self.ProjectName+r".IntPrj\Study Cases.IntPrjfolder\Study Case.IntCase\Scripts\RunSimu.ComDpl"            scriptName=sname+self.ProjectName+r".IntPrj\Study Cases.IntPrjfolder\Study Case.IntCase\Scripts\tests.ComDpl"            print("++++++++ scriptName=",scriptName,file=file_error)            script=self.Project.SearchObject(scriptName)            print("++++++++ script=",script,file=file_error)                        print('PevsFile=',PevsFile,'SoE0File=',SoE0File,' RatepvsFile=',RatepvsFile, 'stepSize=',stepSize,' tstart=',tstart,' tstop=',tstop,file=file_error)            script.SetInputParameterString('PevsFile',PevsFile)            print('before res= PevsFile',file=file_error)            script.SetInputParameterString('SoE0File',SoE0File)            print('before res= SoE0File',file=file_error)            script.SetInputParameterString('RatepvsFile',RatepvsFile)            print('before res= RatepvsFile',file=file_error)            script.SetInputParameterDouble('stepSize', float(stepSize))            print('before res= stepSize',file=file_error)            script.SetInputParameterDouble('startTime', float(tstart))            print('before res= startTime',file=file_error)            script.SetInputParameterDouble('stopTime', float(tstop))                        print('before res= stopTime',file=file_error)                                                                        start = time.time()            res=script.Execute()            end = time.time()            print('Simulation time:',end - start , file=file_error)                                    start2 = time.time()            print('res=',res,file=file_error)            warnings=self.outputWindow.GetContent(self.outputWindow.MessageType.Warn)            errors=self.outputWindow.GetContent(self.outputWindow.MessageType.Error)            errors = []            warnings = []    #        print(warnings)            print(' warnings=',warnings,file=file_error)            print(' errors=',errors,file=file_error)            end2 = time.time()            print('Print Time:', end2-start2, file=file_error)                #        if(np.size(errors)>0):    #            print("errors")     #       elif(np.size(warnings)>0):      #          print("warnings")       #     elif(res>0):        #        print("script problem")         #   else:          #      print('Simulation ok',file=file_error)           #     print("Simulation ok")                                            if(len(errors)>0):                print("errors")                print('executed 1' , file = file_error)            elif(len(warnings)>0):                print("Simulation ok")                print('executed 2' , file = file_error)            elif(res>0):                print("script problem")                print('executed 3' , file = file_error)            else:                print('Simulation ok',file=file_error)                print("Simulation ok")                print('executed 4' , file = file_error)                            if(len(errors)>0 or len(warnings)>0):                console=self.outputWindow.GetContent()                os.makedirs(self.simulationPath)                errorFile=open(self.simulationPath+"\\errors.txt",'w+')                for line in console :                    errorFile.write(line+"\n")                errorFile.close()            self.outputWindow.Clear()            #print(res)        except Exception as err:            print("======> Error ",err,file=file_error)         except:            print("=====> ERROR ",sys.exec_info(),file=file_error)                    test=PowerF()"""try:    ProjectName=r"SimpleGrid"    ElementName=r"EV1"    ElementType=r".ElmGenstat"    app = powerfactory.GetApplicationExt()    app.ActivateProject(ProjectName)     Project=app.GetActiveProject()    print(Project)    app.Show()    Desk = app.GetFromStudyCase("SetDesktop")#Ldf or Sim    app.Show()    Desk.Show("Grid")    print("test 2")except powerfactory.ExitError as error:    print(error)    print('error.code = %d' % error.code)"""