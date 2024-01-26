
# Image Picker for Android

Easy to use and configurable library to Pick an image from the Gallery or Capture image using Camera.






[![1.0.0 Jitpack](https://img.shields.io/badge/Jitpack-1.0.0-green
)](https://jitpack.io/#venkypandi/ImagePicker/1.0.1)
[![1.0.0 Version](https://img.shields.io/badge/release-1.0.0-blue
)](https://github.com/venkypandi/imagePicker/releases)
![Language](https://img.shields.io/badge/language-Kotlin-orange.svg)


# Demo


|                               Profile Image Picker                                |                                    Gallery Only                                    |
|:---------------------------------------------------------------------------------:|:----------------------------------------------------------------------------------:|
| ![](https://github.com/venkypandi/ImagePicker/blob/master/images/camera-demo.gif) | ![](https://github.com/venkypandi/ImagePicker/blob/master/images/gallery-demo.gif) |




## Features

- Pick Gallery Image
- Capture Camera Image
- Handle runtime permission for camera
- Compress Image
- Retrieve Image Result as Uri object
- Retrieve Image Result as File
- Does not require storage permission to pick gallery image or capture new image


## Usage/Examples

1. Add this in your settings.gradle file
```javascript
dependencyResolutionManagement {
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
		repositories {
			mavenCentral()
			maven { url 'https://jitpack.io' }
		}
	}
```

2. Gradle Dependency
```javascript
dependencies {
	        implementation 'com.github.venkypandi:ImagePicker:1.0.1'
	}

```
3. Use ImageUploadComponent in any layout
```javascript
<com.venkatesh.imagepicker.ImageUploadComponent
        android:id="@+id/uploadComponent"
        android:layout_width="match_parent"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        android:layout_height="wrap_content"/>

```

4. Use ImageUploadComponent configuration any activity or fragment.

```javascript
  var imageUploadComponent = findViewById(R.id.uploadComponent)

```
or incase using viewbinding

```javascript
var imagebinding = binding.uploadComponent
```

5. To get the image as uri or file

```javascript
imageUploadComponent.findViewById<Button>(com.venkatesh.imagepicker.R.id.submitButton).setOnClickListener {
            val selectedImageUri = imageUploadComponent.selectedImageUri
}

```

//optional

6. To compress the image 
```javascript
 val file = selectedImageUri.toFile()
                    //optional
                    val compressedImage = imageUploadComponent.compressImage(file.path)
```

7. To upload the image as file to server

```javascript
val repository = ImageRepositoryImpl(RetrofitClient.apiService)
```
create your own retrofit client and Api service and pass as a parameter in the ImageRepositoryImpl. Refer the aboce demo project implementing this feature.



## Customization
1. To show/hide preview button
```javascript
imageUploadComponent.showPreviewButton(true)
```
2. To show/hide select button
```javascript
imageUploadComponent.showSelectButton(true)
```
3. To show/hide submit button
```javascript
imageUploadComponent.showSubmitButton(true)
```
4. To show/hide FileType
```javascript
imageUploadComponent.showFileType(true)
```
5. To show/hide Filename
```javascript
imageUploadComponent.showFileName(true)
```
By default, all the above values are true, if you want to hide pass false.
## Compatibility
- Library - Android Nougat 7.0+ (API 24)
- Sample - Android Nougat 7.0+ (API 24)