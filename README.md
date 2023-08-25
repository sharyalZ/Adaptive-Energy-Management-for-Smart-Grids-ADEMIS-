<p align="center">
  <img width="250" height="100" src="https://user-images.githubusercontent.com/73366653/97095321-45e05a00-165e-11eb-9c60-b90723caba4a.png">
  <img width="250" height="100" src="https://user-images.githubusercontent.com/73366653/97095369-b8513a00-165e-11eb-9ef2-e2fcef626f88.jpeg">
  <img width="150" height="100" src="https://user-images.githubusercontent.com/73366653/97095285-c0f54080-165d-11eb-82bf-e0c032a1e333.png">    
  <img width="110" height="110" src="https://user-images.githubusercontent.com/73366653/97095257-825f8600-165d-11eb-8704-c998a9fae1ce.png"> 
   
</p>

# Adaptive-Energy-Management-in-Smart-Grids(ADEMIS)
ADaptive Energy Management in Smart Grids(ADEMIS) is an active smart numerical platform, which can handle issues arising in the grid. The system is developed to deploy the multi-agent systems in the field of smart grids. Current version of the system handles grid issues such as line congestion, voltage limits violations, and energy mismatch of the balance responsible party(BRP). It is based on AMAK platform, which is based in JAVA and has been developed in Eclipse. For the power systems calculations, PowerFactory is used. A communication link has been set-up using Pyhton. In the future, other open source power calculations tools' compatibility will be tested.

## Required Softwares and Packages
Software Package  | Version 
------------- | -------------
[Eclipse](https://www.eclipse.org/downloads/)  | 2020-09
[Python](https://www.python.org/downloads/release/python-360/)  | 3.6.0(32 bit)
[PowerFactory](https://www.digsilent.de/en/downloads.html)  | Latest
[Java](https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html)  | 1.8.0
[NumPy](https://numpy.org/)  | Latest
[pywin32](https://pypi.org/project/pywin32/)  | Latest 

## ADEMIS Installation Procedure 
To start using the ADEMIS platform on your personal computer, follow the follwing steps in order. All the required software packages can be downloaded by clicking on the links above. The procedure is divided into 2 steps. Step 1 is installing required software packages and step 2 is on how to set-up the project and start using it.

### Step 1: Software Packages Installation
1. Install Python v3.6.0(32-bit). To check if the installation was successful, open CMD and type ***python --version***. Installed python version will be displayed. 
![Capture](https://user-images.githubusercontent.com/73366653/97092630-ca72ae80-1645-11eb-81de-6096057e924e.PNG)

1. Next, install NumPy and pywin32 on your computer. To do so open CMD again and type ***pip install numpy*** and then ***pip install pywin32*** to install mentioned packages respectively. To verify installation ***pip show numpy*** and ***pip show pywin32*** commands can be used as shown. 
![Capture](https://user-images.githubusercontent.com/73366653/97092608-913a3e80-1645-11eb-90ce-3835afb33aa9.PNG)

1. Next install Java runtime environment. It should be version 1.8.0(jdk8). After installtion verify its installation in cmd using ***java -version***. 
![Capture](https://user-images.githubusercontent.com/73366653/97092818-589b6480-1647-11eb-9c17-f3ee7fa81094.PNG)

1. Install Eclipse IDE for Java developers. 

1. Install PowerFactory and activate it using your license key. 

### Step 2: Project Set-up
1. Download the complete project files by clicking on the ***Code*** button on the top, and selecting ***Dowload Zip*** option. 
<img width="250" height="200" src="https://user-images.githubusercontent.com/73366653/97093684-6a343a80-164e-11eb-84ac-3a3381532f19.png">

2. Extract the .zip file. It contains the ADEMIS project folder and PowerFactory design projects to perform required power analysis. 

1. Open the PowerFactory project on which multi-agent simulation is desired to be performed using ADEMIS. 

1. After the project is activated in PowerFactory(run PowerFactory as administrator), open the ***Data Manager*** tab. Navigate to ***%User Name% -> %Projet Name% -> Study Cases -> Study Case***. In the ***Study Case*** folder, two sub-folders will be present named ***Results*** and ***Scripts***. This contains the details of output files of PowerFactory simulations. A valid path to store these files has to be added in all files present inside ***Results*** and ***Scripts*** folders. To add the path, right click on the file and select ***Edit***. Enter the desired path in the input field with tag the ***File Name*** and press ***Execute***. Repeat this step to add this path to all the files in the twoo mentioned sub-folders. 
(***Note***: User can select any valid path in the computer; all input fields with tag ***File Name*** should be filled with this path in both ***Results*** and ***Scripts*** folders' files). 
![Capture](https://user-images.githubusercontent.com/73366653/97095237-344a8280-165d-11eb-8655-1e20084899c0.PNG)


1. Close PowerFactory. Navigate to the folder where PowerFactory is installed(usually inside C drive program files). In the folder look for the folder named ***python***. Copy the path of this folder. Now open the unzipped folder and navigate to ***Workspace -> MAS_ADEMIS -> ademis***. A python script named ***InterfaceJavaPowerFactory*** will be present. Open it in any editor. In line number 37, replace the existing path with the path of python folder you copied(dont' erase the "//3.6" at the end). Save the changes and close it. 
![Capture](https://user-images.githubusercontent.com/73366653/97095938-c191d500-1665-11eb-9846-75c37bf5db91.PNG)

1. Now start Eclipse IDE for Java developers. A window will appear to specify workspace path. Click on ***Browse*** and give this path as workspace path: Inside the unzipped project folder go insider the ***Workspace*** folder and press ***Select Folder*** and then finish this step by pressing ***Launch***. 

1. Eclipse will start. A wrokspace should already be present with all the required folders as show below. If not, you can add the reauired project folders manually as well(***Note***: Make sure the naming is the same for all folders).
![Capture](https://user-images.githubusercontent.com/73366653/97096203-e20f5e80-1668-11eb-8306-ea5c8d24268e.PNG)

1. To verfiy compiler settings, right click on the first folder ***ademis*** and then select properties. A new properties window will open for this project folder. Go to ***Java Compiler*** menu and check the tickbox for ***Enable project specific settings*** and select ***1.8*** from the dropdown menu of ***Compiler compliance level***. Press ***Apply*** button to save the changes. Repeat the same steps for every project folder(i.e. 'amak', 'gui', 'lx-plot', 'resultsgui' and 'test').
![Capture](https://user-images.githubusercontent.com/73366653/97096277-0e77aa80-166a-11eb-8ecf-1c08490e384f.PNG)

1. Finally, the Java build paths have to be specified for each project folder. Right click in the first folder i.e. ademis, and select properties. Go to the ***Java Build Path*** menu. Clcik on the ***Projects*** tab. For this specific folder, ***amak*** and ***lx-plot*** should be present in there. If not you can add them by using ***Add*** button on the right. Press the ***Apply*** button and close this properties menu. Every project folder(i.e. 'amak', 'gui', 'lx-plot', 'resultsgui' and 'test') needs specific projects in the build path. A list of project folder with required build paths is given below. If these are not already present in the build path, they should be added. 

    Project Folder  | Required Paths/Projects
    ------------- | -------------
    ademis  | amak, lx-plot
    amak  |  - 
    gui  | ademis, resultsgui
    lx-plot | -
    resultsgui  | ademis, lx-plot
    test  | -

  ![Capture](https://user-images.githubusercontent.com/73366653/97096574-6a403480-1666-11eb-8048-455f18345b66.PNG)

10. Make sure that the path ***C:\\Users\\%username%\\AppData\\Local\\Programs\\Python\\Python36-32\\python*** exists for Python36-32 directory. Otherwise, you can find the path for this folder and paste it in the ***PowerFactoryCaller*** file, present inside ***ademis -> src -> Interface***. 
![Capture](https://user-images.githubusercontent.com/73366653/97096697-c6f01f00-1667-11eb-900a-514643f32595.PNG)

1. ***Re-build*** the complete workspace and press ***Run***. ADEMIS is running and ready to use! 
![Capture](https://user-images.githubusercontent.com/73366653/97096770-965cb500-1668-11eb-8467-d39c1f31f97b.PNG)


#### In case of any query, feel free to contact at [sharyal.zafar@ens-rennes.fr](sharyal.zafar@ens-rennes.fr)
