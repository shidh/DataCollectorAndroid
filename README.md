DataCollectorAndroid
=========================

System Architecture Overview
-------------
![Alt text](https://cloud.githubusercontent.com/assets/1011537/9063371/47fefd82-3ac7-11e5-853a-5bbd22268d76.jpg
"System Architecture Overview")

E-R Diagram
-------------
![Alt text](https://cloud.githubusercontent.com/assets/1011537/9063385/5ada69aa-3ac7-11e5-901d-8cafede85bd1.jpg
"E-R Diagram")

## Third Party Libraries

    compile fileTree(dir: 'libs', include: ['*.jar'])
    compile 'com.android.support:design:22.2.1'
    compile 'com.squareup.retrofit:retrofit:1.9.0'
    compile 'com.squareup.okhttp:okhttp-urlconnection:2.0.0'
    compile 'com.squareup.okhttp:okhttp:2.0.0'
    compile 'com.squareup.picasso:picasso:2.5.2'
    compile 'com.google.android.gms:play-services:7.5.0'
    compile 'com.squareup:otto:1.3.6'
    compile 'com.michaelpardo:activeandroid:3.1.0-SNAPSHOT'
    compile 'com.melnykov:floatingactionbutton:1.3.0'
    compile 'com.github.dmytrodanylyk.circular-progress-button:library:1.1.3'
    compile 'com.github.dmytrodanylyk.android-process-button:library:1.0.3'
    compile 'com.oguzdev:CircularFloatingActionMenu:1.0.2'
    compile 'com.android.support:recyclerview-v7:22.2.1'
    compile 'com.daimajia.swipelayout:library:1.2.0@aar'
    compile 'com.daimajia.androidanimations:library:1.1.2@aar'
    compile 'com.etsy.android.grid:library:1.0.5'
    compile('com.mikepenz:materialdrawer:3.0.2@aar') {
        transitive = true
    }
    compile project(':androidRecording')
    compile 'com.github.chrisbanes.photoview:library:1.2.4'
    compile 'com.github.tuesda:CircleRefreshLayout:92a27db47d'

## TODOs
    1: Change the datamodel, delete the specific metadata requirements
    2: Add a drawer View and put login view/ settings link into it
    3: Add Collection View
    4: Add swipe delete effect
    5: rippleeffect
    
License
=======
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
    
