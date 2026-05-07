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

    # Fix View with double layout_height
    content = content.replace('android:layout_height="wrap_content"\n                android:layout_marginBottom="24dp"\n                android:layout_height="1dp"', 'android:layout_height="1dp"\n                android:layout_marginBottom="24dp"')
    content = content.replace('android:layout_height="wrap_content"\n            android:layout_marginTop="12dp"\n            android:layout_marginBottom="4dp"\n            android:layout_height="1dp"', 'android:layout_height="1dp"\n            android:layout_marginTop="12dp"\n            android:layout_marginBottom="4dp"')
    content = content.replace('android:layout_height="wrap_content"\n            android:layout_marginBottom="8dp"\n            android:layout_height="1dp"', 'android:layout_height="1dp"\n            android:layout_marginBottom="8dp"')
    content = content.replace('android:layout_height="wrap_content"\n            android:layout_marginBottom="12dp"\n            android:layout_height="1dp"', 'android:layout_height="1dp"\n            android:layout_marginBottom="12dp"')
    content = re.sub(r'android:layout_height="wrap_content"(\s*)android:layout_marginTop="12dp"(\s*)android:layout_marginBottom="4dp"(\s*)android:layout_height="1dp"', r'android:layout_height="1dp"\1android:layout_marginTop="12dp"\2android:layout_marginBottom="4dp"', content)
    content = re.sub(r'android:layout_height="wrap_content"\s+android:layout_marginBottom="24dp"\s+android:layout_height="1dp"', r'android:layout_height="1dp"\n                android:layout_marginBottom="24dp"', content)
    
    # Just in case, remove any lingering double layout_heights in <View
    content = re.sub(r'<View([\s\S]*?)android:layout_height="wrap_content"([\s\S]*?)android:layout_height="1dp"', r'<View\1\2android:layout_height="1dp"', content)
    
    # Add styling to TextInputEditText in bottom sheets
    if 'bottom_sheet' in file_name:
        content = re.sub(r'<com.google.android.material.textfield.TextInputEditText\s+android:id="([^"]+)"\s+android:layout_width="match_parent"\s+android:layout_height="wrap_content"\s+android:inputType="([^"]+)"\s*/>', r'<com.google.android.material.textfield.TextInputEditText\n                android:id="\1"\n                android:layout_width="match_parent"\n                android:layout_height="wrap_content"\n                android:inputType="\2"\n                android:textSize="13sp"\n                android:textColor="#1A2535"\n                android:fontFamily="sans-serif" />', content)
        
        # for inputs with no inputType (e.g. none)
        content = re.sub(r'<com.google.android.material.textfield.TextInputEditText\s+android:id="([^"]+)"\s+android:layout_width="match_parent"\s+android:layout_height="wrap_content"\s+android:focusable="([^"]+)"\s+android:clickable="([^"]+)"\s+android:inputType="([^"]+)"\s*/>', r'<com.google.android.material.textfield.TextInputEditText\n                android:id="\1"\n                android:layout_width="match_parent"\n                android:layout_height="wrap_content"\n                android:focusable="\2"\n                android:clickable="\3"\n                android:inputType="\4"\n                android:textSize="13sp"\n                android:textColor="#1A2535"\n                android:fontFamily="sans-serif" />', content)

    with open(path, 'w', encoding='utf-8') as f:
        f.write(content)
    print(f'Processed {file_name}')

