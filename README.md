# ASFLibrary

ASFLibrary is an Android SFTP Library.

## Features

- Upload file to sftp server
- Get file from stfp server
- Delete, rename file or directory in sftp server
- new folder in sftp server
- Can list file and directory information in sftp server
- Can set process monitor to do something in every periods when data transferring
- Can set login result deal function

## Usage

At first, you should download source code in you computer throught git, besides, you can use `*.arr` file which can import to your project through, Android Studio please check [release page](https://github.com/GMA-Team/ASFLibrary/releases)
```shell
mkdir workspace
cd workspace
git clone https://github.com/GMA-Team/ASFLibrary.git
```
### Import

open Android Studio, click
```
File-New-Import Modules
```
add this into `build.gradle(Module:app)` any syc it.
```
implementation project(path: ':asflibrary')
```
## Quick Start

New a project and add those `MainActivity.java`. This Activity can login the sftp server and print all file information in directory `/home/cpi`, of course, you can modify parameters in code according to notes in order to show your directory in your sftp server.

```java
package com.wangtingzheng.gmam;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.wangtingzheng.asflibrary.SFTP;
import com.wangtingzheng.asflibrary.Utils.ListResultDealUtils;
import com.wangtingzheng.asflibrary.Utils.LoginDealUtils;
import java.util.Enumeration;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SFTP sftp  = new SFTP("cpi", "cpi", "192.168.1.40", 22);   //fill with sftp login information

        sftp.setLoginDeal(new LoginDealUtils(){ //set login result deal function
            @Override
            public void  dealLoginResult(boolean result){
                System.out.println(result);
            }
        });
        sftp.setListResultDeal(new ListResultDealUtils(){ //set list reuslt deal function to print information
            @Override
            public void dealListResult(Vector<?> result){
                Enumeration<?> elements = result.elements();
                while (elements.hasMoreElements()) {
                    System.out.println(elements.nextElement());
                }
            }
        });
        sftp.SFTP_List("/home/cpi");  // set the directory and do the action
    }
}
```
More function and features usage can be found in [Wiki](https://github.com/GMA-Team/ASFLibrary/wiki).

## Library

- [Commons IO](http://commons.apache.org/proper/commons-io/)
- [JSch - Java Secure Channel](http://www.jcraft.com/jsch/)