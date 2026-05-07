import os
import re

dir_path = 'app/src/main/res/layout/'

for file_name in os.listdir(dir_path):
    if not file_name.endswith('.xml'):
        continue
        
    path = os.path.join(dir_path, file_name)
    with open(path, 'r', encoding='utf-8') as f:
        content = f.read()

    # Function to remove duplicate fontFamily attributes inside a tag
    def remove_dup_font_family(match):
        tag_content = match.group(0)
        # Find all occurrences of android:fontFamily="sans-serif-medium"
        occurrences = re.findall(r'\s*android:fontFamily="sans-serif-medium"', tag_content)
        if len(occurrences) > 1:
            # Keep the first one, remove the rest
            first = True
            def replacer(m):
                nonlocal first
                if first:
                    first = False
                    return m.group(0)
                return ''
            tag_content = re.sub(r'\s*android:fontFamily="sans-serif-medium"', replacer, tag_content)
        return tag_content
        
    def remove_dup_icon_tint(match):
        tag_content = match.group(0)
        occurrences = re.findall(r'\s*app:iconTint="[^"]+"', tag_content)
        if len(occurrences) > 1:
            # Keep the last one, as the last one was probably the one we added (#EF4444 or #64748B)
            # Actually, to be safe, just remove app:iconTint="#64748B" if there is another app:iconTint
            if 'app:iconTint="#EF4444"' in tag_content and 'app:iconTint="#64748B"' in tag_content:
                tag_content = tag_content.replace('app:iconTint="#64748B"', '', 1)
        return tag_content

    new_content = re.sub(r'<TextView[^>]+>', remove_dup_font_family, content)
    new_content = re.sub(r'<com.google.android.material.button.MaterialButton[^>]+>', remove_dup_icon_tint, new_content)
    new_content = re.sub(r'<com.google.android.material.button.MaterialButton[^>]+>', remove_dup_font_family, new_content)

    if new_content != content:
        with open(path, 'w', encoding='utf-8') as f:
            f.write(new_content)
        print(f'Fixed duplicates in {file_name}')

