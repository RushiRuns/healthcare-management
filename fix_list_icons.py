import os
import re

dir_path = 'app/src/main/res/layout/'

items = [f for f in os.listdir(dir_path) if f.startswith('item_') and f.endswith('.xml')]

for file_name in items:
    path = os.path.join(dir_path, file_name)
    with open(path, 'r', encoding='utf-8') as f:
        content = f.read()

    def replace_button(match):
        block = match.group(0)
        
        # Extract ID
        id_match = re.search(r'android:id="([^"]+)"', block)
        btn_id = id_match.group(1) if id_match else ''
        
        # Extract src from app:icon
        icon_match = re.search(r'app:icon="([^"]+)"', block)
        src = icon_match.group(1) if icon_match else ''
        
        # Determine tint
        tint = '#64748B'
        if 'ic_delete' in src or '#EF4444' in block:
            tint = '#EF4444'
            
        desc_match = re.search(r'android:contentDescription="([^"]+)"', block)
        desc = desc_match.group(1) if desc_match else ''
        
        margin_end = 'android:layout_marginEnd="12dp"\n                ' if 'ic_edit' in src else ''
        
        return f'''<ImageView
                android:id="{btn_id}"
                android:layout_width="32dp"
                android:layout_height="32dp"
                {margin_end}android:padding="4dp"
                android:background="?attr/selectableItemBackgroundBorderless"
                android:contentDescription="{desc}"
                android:src="{src}"
                app:tint="{tint}" />'''

    new_content = re.sub(r'<com\.google\.android\.material\.button\.MaterialButton[^>]+ic_edit_24[^>]*/>', replace_button, content)
    new_content = re.sub(r'<com\.google\.android\.material\.button\.MaterialButton[^>]+ic_delete_24[^>]*/>', replace_button, new_content)

    if new_content != content:
        with open(path, 'w', encoding='utf-8') as f:
            f.write(new_content)
        print(f'Processed {file_name}')

