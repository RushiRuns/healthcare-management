import os
import re

dir_path = 'app/src/main/res/layout/'

bottom_sheets = [f for f in os.listdir(dir_path) if f.startswith('bottom_sheet_') and f.endswith('.xml')]

for file_name in bottom_sheets:
    path = os.path.join(dir_path, file_name)
    with open(path, 'r', encoding='utf-8') as f:
        content = f.read()

    # Fix MaterialButton in bottom sheets
    def fix_button(match):
        btn = match.group(0)
        # We want to force height to 48dp, cornerRadius to 8dp, elevation to 0dp, backgroundTint to #185FA5, textColor to #FFFFFF
        btn = re.sub(r'android:layout_height="[^"]+"', 'android:layout_height="48dp"', btn)
        btn = re.sub(r'app:cornerRadius="[^"]+"', 'app:cornerRadius="8dp"', btn)
        
        # Add missing attributes if not present
        if 'app:elevation=' not in btn:
            btn = btn.replace('android:layout_height="48dp"', 'android:layout_height="48dp"\n        app:elevation="0dp"')
        if 'app:backgroundTint=' not in btn:
            btn = btn.replace('android:layout_height="48dp"', 'android:layout_height="48dp"\n        app:backgroundTint="#185FA5"')
        if 'android:textColor=' not in btn:
            btn = btn.replace('android:layout_height="48dp"', 'android:layout_height="48dp"\n        android:textColor="#FFFFFF"')
        if 'android:fontFamily=' not in btn:
            btn = btn.replace('android:layout_height="48dp"', 'android:layout_height="48dp"\n        android:fontFamily="sans-serif-medium"')
        if 'android:textAllCaps=' not in btn:
            btn = btn.replace('android:layout_height="48dp"', 'android:layout_height="48dp"\n        android:textAllCaps="false"')
        return btn

    content = re.sub(r'<com\.google\.android\.material\.button\.MaterialButton[^>]+>', fix_button, content)

    # Fix TextView Title colors (often size 22sp or 18sp)
    # Replace ?android:attr/textColorPrimary with #1A2535
    content = content.replace('?android:attr/textColorPrimary', '#1A2535')
    
    # In bottom_sheet_add_lab.xml (and others), make sure the background of the Root isn't dark
    # Actually, light theme handles the background. But let's set it to #FFFFFF just in case
    # If it's a LinearLayout or NestedScrollView, we can set android:background="#FFFFFF"
    
    if new_content := content:
        with open(path, 'w', encoding='utf-8') as f:
            f.write(new_content)
        print(f'Processed {file_name}')

