/**
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.huawei.mlkit.sample.photoreader.java;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.huawei.mlkit.lensengine.BitmapUtils;

import java.io.Serializable;

import static com.huawei.mlkit.sample.photoreader.Constant.EXTRA_IMAGE_PATH;

public class ReadPhotoActivityContracts {

    static class ChoosePictureContract extends ActivityResultContract<Void, BitmapFactory> {

        @NonNull
        @Override
        public Intent createIntent(@NonNull Context context, Void input) {
            final Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            return intent;
        }

        @Nullable
        @Override
        public BitmapFactory parseResult(int resultCode, @Nullable Intent intent) {
            return intent != null ? new MediaDataBitmapFactory(intent.getData()) : null;
        }
    }

    static class TakePictureContract extends ActivityResultContract<Void, BitmapFactory> {

        @NonNull
        @Override
        public Intent createIntent(@NonNull Context context, Void input) {
            return new Intent(context, CapturePhotoActivity.class);
        }

        @Override
        public BitmapFactory parseResult(int resultCode, @Nullable Intent intent) {
            if(intent == null || intent.getStringExtra(EXTRA_IMAGE_PATH) == null) {
                return null;
            }

            final String path = intent.getStringExtra(EXTRA_IMAGE_PATH);
            return new CameraPictureBitmapFactory(path);
        }
    }

    interface BitmapFactory extends Serializable {
        @NonNull
        Bitmap buildBitmap(final ContentResolver contentResolver, final int width, final int height);
    }

    private static class CameraPictureBitmapFactory implements BitmapFactory {

        private final String picturePath;

        private CameraPictureBitmapFactory(String picturePath) {
            this.picturePath = picturePath;
        }

        @NonNull
        @Override
        public Bitmap buildBitmap(ContentResolver contentResolver, int width, int height) {
            return BitmapUtils.loadFromFilePath(picturePath, width, height);
        }
    }

    private static class MediaDataBitmapFactory implements BitmapFactory {

        private final Uri fileUri;

        private MediaDataBitmapFactory(Uri fileUri) {
            this.fileUri = fileUri;
        }

        @NonNull
        @Override
        public Bitmap buildBitmap(ContentResolver contentResolver, int width, int height) {
            return BitmapUtils.loadFromMediaUri(contentResolver, fileUri, width, height);
        }
    }
}
