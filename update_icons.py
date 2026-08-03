import os
import xml.etree.ElementTree as ET

base_dir = "/home/zero/AndroidStudioProjects/LG_Extended/app/src/main/res/drawable"

icons_colors = {
    "ic_red_e_internet": "#007AFF",
    "ic_bluetooth": "#007AFF",
    "ic_sonido": "#FF2D55",
    "ic_notificaciones": "#FF3B30",
    "ic_pantalla": "#007AFF",
    "ic_fondo_pantalla_y_tema": "#AF52DE",
    "ic_pantalla_de_bloqueo_y_seguridad": "#4CD964",
    "ic_privacidad": "#007AFF",
    "ic_ubicacion": "#007AFF",
    "ic_extensiones": "#FF9500",
    "ic_aplicaciones": "#5856D6",
    "ic_bienestar_digital": "#5856D6",
    "ic_bateria": "#34C759",
    "ic_almacenamiento": "#8E8E93",
    "ic_seguridad_y_emergencia": "#FF3B30",
    "ic_cuentas": "#8E8E93",
    "ic_sistema": "#8E8E93",
    "ic_accesibilidad": "#007AFF"
}

ET.register_namespace('android', 'http://schemas.android.com/apk/res/android')
ns = {'android': 'http://schemas.android.com/apk/res/android'}

circle_path_data = 'M12,12m-11,0a11,11 0,1 1,22 0a11,11 0,1 1,-22 0'
squircle_path_data = 'M 6,1 L 18,1 C 20.76,1 23,3.24 23,6 L 23,18 C 23,20.76 20.76,23 18,23 L 6,23 C 3.24,23 1,20.76 1,18 L 1,6 C 1,3.24 3.24,1 6,1 Z'

for icon, new_color in icons_colors.items():
    file_path = os.path.join(base_dir, f"{icon}.xml")
    if not os.path.exists(file_path):
        print(f"File not found: {file_path}")
        continue
        
    tree = ET.parse(file_path)
    root = tree.getroot()
    
    modified = False
    for path in root.findall('.//path'):
        path_data = path.get('{http://schemas.android.com/apk/res/android}pathData')
        if path_data == circle_path_data or path_data == circle_path_data.replace(" ", ""):
            path.set('{http://schemas.android.com/apk/res/android}pathData', squircle_path_data)
            path.set('{http://schemas.android.com/apk/res/android}fillColor', new_color)
            modified = True
            break
            
    if modified:
        tree.write(file_path, encoding='utf-8', xml_declaration=True)
        print(f"Updated {icon}.xml")
    else:
        print(f"Circle path not found in {icon}.xml")
