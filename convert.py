import os
import re

files_to_process = [
    'item_history.xml', 'item_rx.xml', 'item_note.xml', 'item_vitals.xml', 'item_lab.xml',
    'fragment_overview.xml', 'fragment_rx.xml', 'fragment_notes.xml', 'fragment_vitals.xml', 'fragment_labs.xml',
    'bottom_sheet_add_condition.xml', 'bottom_sheet_add_prescription.xml', 'bottom_sheet_consultation_note.xml', 'bottom_sheet_add_vitals.xml', 'bottom_sheet_add_lab.xml'
]

dir_path = 'app/src/main/res/layout/'

for file_name in files_to_process:
    path = os.path.join(dir_path, file_name)
    if not os.path.exists(path):
        print(f'File not found: {path}')
        continue
        
    with open(path, 'r', encoding='utf-8') as f:
        content = f.read()

    # 1. Colors
    content = content.replace('?attr/colorOnSurfaceVariant', '#64748B')
    content = content.replace('?attr/colorOnSurface', '#1A2535')
    content = content.replace('?attr/colorPrimary', '#185FA5')
    content = content.replace('?attr/colorSecondary', '#64748B')
    content = content.replace('?attr/colorOutlineVariant', '#E2E8F0')
    content = content.replace('?attr/colorError', '#EF4444')
    
    # 2. MaterialCardView
    content = re.sub(r'app:cardElevation=[\"\'].*?[\"\']', 'app:cardElevation="0dp"\n    app:cardBackgroundColor="#FFFFFF"\n    app:strokeColor="#E2E8F0"', content)
    content = re.sub(r'app:strokeWidth=[\"\'].*?[\"\']', 'app:strokeWidth="1dp"', content)
    content = re.sub(r'app:cardCornerRadius=[\"\'].*?[\"\']', 'app:cardCornerRadius="8dp"', content)
    
    # 3. Typography
    content = content.replace('android:textStyle="bold"', 'android:fontFamily="sans-serif-medium"')
    content = re.sub(r'android:textAppearance="\?attr/textAppearanceTitleMedium"', 'android:textSize="18sp"\n            android:fontFamily="sans-serif-medium"', content)
    content = re.sub(r'android:textAppearance="\?attr/textAppearanceBodyLarge"', 'android:textSize="15sp"\n            android:fontFamily="sans-serif-medium"', content)
    content = re.sub(r'android:textAppearance="\?attr/textAppearanceBodyMedium"', 'android:textSize="13sp"\n            android:fontFamily="sans-serif"', content)
    content = re.sub(r'android:textAppearance="\?attr/textAppearanceLabelMedium"', 'android:textSize="11sp"\n            android:fontFamily="sans-serif-medium"', content)
    content = re.sub(r'android:textAppearance="\?attr/textAppearanceLabelSmall"', 'android:textSize="11sp"\n            android:fontFamily="sans-serif-medium"', content)
    content = re.sub(r'android:textAppearance="\?attr/textAppearanceLabelLarge"', 'android:textSize="13sp"\n            android:fontFamily="sans-serif-medium"', content)
    content = re.sub(r'android:textAppearance="\?attr/textAppearanceHeadlineSmall"', 'android:textSize="18sp"\n            android:fontFamily="sans-serif-medium"', content)

    # 4. Dividers
    content = content.replace('<com.google.android.material.divider.MaterialDivider', '<View\n            android:background="#E2E8F0"')
    content = re.sub(r'app:dividerColor=[\"\'].*?[\"\']', 'android:layout_height="1dp"', content)
    
    # 5. Icons & Buttons in lists
    content = content.replace('style="@style/Widget.Material3.Button.IconButton"', 'style="@style/Widget.MaterialComponents.Button.TextButton"\n                app:iconTint="#64748B"')
    
    # 6. FABs
    if 'ExtendedFloatingActionButton' in content:
        content = re.sub(r'<com\.google\.android\.material\.floatingactionbutton\.ExtendedFloatingActionButton(.*?)/?>', 
                         r'<com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton\1\n        app:backgroundTint="#185FA5"\n        android:textColor="#FFFFFF"\n        app:iconTint="#FFFFFF"\n        app:elevation="0dp"\n        android:fontFamily="sans-serif-medium" />', content, flags=re.DOTALL)
                         
    # 7. Bottom Sheets Inputs
    if 'bottom_sheet' in file_name:
        content = content.replace('style="@style/Widget.Material3.TextInputLayout.OutlinedBox"', 
                                  'style="@style/Widget.MaterialComponents.TextInputLayout.OutlinedBox.Dense"\n            android:textColorHint="#64748B"\n            app:boxBackgroundColor="#FFFFFF"\n            app:boxStrokeColor="#E2E8F0"\n            app:boxStrokeWidth="1dp"\n            app:boxCornerRadiusTopStart="8dp"\n            app:boxCornerRadiusTopEnd="8dp"\n            app:boxCornerRadiusBottomStart="8dp"\n            app:boxCornerRadiusBottomEnd="8dp"\n            app:hintTextColor="#185FA5"')
        content = re.sub(r'android:layout_height="56dp"', 'android:layout_height="48dp"', content)
        content = re.sub(r'app:cornerRadius="12dp"', 'app:cornerRadius="8dp"\n            app:elevation="0dp"\n            app:backgroundTint="#185FA5"\n            android:textColor="#FFFFFF"\n            android:textAllCaps="false"\n            android:fontFamily="sans-serif-medium"', content)

    with open(path, 'w', encoding='utf-8') as f:
        f.write(content)
    print(f'Processed {file_name}')

