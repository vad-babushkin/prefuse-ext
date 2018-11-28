import sys
import csv
import json
import xml.etree.ElementTree as et

def json_to_xml(fname):
    with open(fname) as f:
        data = json.loads(f.read())

    # Convert doctors tree to XML
    doc_root = et.Element('tree')

    decls = et.SubElement(doc_root, 'declarations')
    decl_name = et.SubElement(decls, 'attributeDecl')
    decl_name.set('name', 'name')
    decl_name.set('type', 'String')

    decl_institution = et.SubElement(decls, 'attributeDecl')
    decl_institution.set('name', 'institutions')
    decl_institution.set('type', 'String')

    traverse_doc_tree(data['doctors_root'], doc_root)


    base_name = fname.split(".json")[0]

    doc_doc = et.ElementTree(doc_root)
    doc_doc.write('%s.xml' % base_name)


#     doc_doc.write('%s_docs.xml' % base_name)
#
#     # Convert universities list to CSV
#     with open('%s_unis.csv' % base_name, 'w') as f:
#         csv_out = csv.writer(f, )
#         csv_out.writerow(['name', 'doctors'])
#
#         for uni in data['universities']:
#             docs = uni.get('doctors_institutions')
#             csv_out.writerow(
#                 map(lambda s: s.encode('utf-8'),
#                     [ uni['name'],
#                       "none" if docs is None or len(docs) == 0 else
#                       '::'.join(map(safe_get_name, docs))
#                     ])
#                 )


def safe_get_name(n):
    if isinstance(n, dict):
        return n['name']
    else:
        return unicode(n)


def traverse_doc_tree(root, parent_html):
    if (root['children']) == 0:
        node = et.SubElement(parent_html, 'leaf')
    else:
        node = et.SubElement(parent_html, 'branch')
    attr_name = et.SubElement(node, 'attribute')
    attr_name.set('name', 'name')
    attr_name.set('value', root['name'])

    attr_institution = et.SubElement(node, 'attribute')
    attr_institution.set('name', 'institutions')
    attr_institution.set('value',
            "none" if len(root['institutions']) == 0 else
            '::'.join(map(safe_get_name, root['institutions'])))

    for child in root['children']:
        traverse_doc_tree(child, node)

if __name__ == '__main__':
    for data_json in sys.argv[1:]:
        try:
            if data_json.endswith(".json"):
                json_to_xml(data_json)
        except:
            print("error in file %s" % data_json, sys.exc_info()[0])
        else:
            print("processed %s" % data_json)
