from flask import Flask, jsonify, request, abort
from flask_cors import CORS
import mysql.connector
from mysql.connector import Error

app = Flask(__name__)
CORS(app) 

db_config = {
    'host': 'localhost',
    'user': 'root',    
    'password': 'root',      
    'database': 'sistema_inventario'
}

def get_db_connection():
    """Establece y retorna una conexión a la base de datos."""
    try:
        connection = mysql.connector.connect(**db_config)
        return connection
    except Error as e:
        print(f"Error al conectar a MySQL: {e}")
        return None

@app.route('/api/books', methods=['GET'])
def get_products():
    conn = get_db_connection()
    if conn is None:
        return jsonify({'error': 'Error interno del servidor (BD)'}), 500
    
    cursor = conn.cursor(dictionary=True) 
    cursor.execute("SELECT * FROM productos")
    productos = cursor.fetchall()
    
    cursor.close()
    conn.close()
    return jsonify(productos), 200

@app.route('/api/books', methods=['POST'])
def create_product():
    if not request.json or not 'title' in request.json:
        abort(400)
        
    conn = get_db_connection()
    if conn is None:
        return jsonify({'error': 'Error de conexión con la BD'}), 500
        
    cursor = conn.cursor(dictionary=True)
    query = "INSERT INTO productos (title, author, genre, year) VALUES (%s, %s, %s, %s)"
    valores = (
        request.json['title'],
        request.json.get('author', ''),
        request.json.get('genre', ''),
        request.json.get('year', 0)
    )
    
    cursor.execute(query, valores)
    conn.commit()
    nuevo_id = cursor.lastrowid
    
    cursor.close()
    conn.close()
    
    return jsonify({
        'id': nuevo_id,
        'title': request.json['title'],
        'author': request.json.get('author', ''),
        'genre': request.json.get('genre', ''),
        'year': request.json.get('year', 0)
    }), 201

@app.route('/api/books/<int:product_id>', methods=['PUT'])
def update_product(product_id):
    if not request.json:
        abort(400)
        
    conn = get_db_connection()
    if conn is None:
        return jsonify({'error': 'Error de conexión con la BD'}), 500
        
    cursor = conn.cursor()
    
    cursor.execute("SELECT * FROM productos WHERE id = %s", (product_id,))
    if cursor.fetchone() is None:
        cursor.close()
        conn.close()
        abort(404)
        
    # Actualizamos el registro
    query = "UPDATE productos SET title=%s, author=%s, genre=%s, year=%s WHERE id=%s"
    valores = (
        request.json.get('title'),
        request.json.get('author'),
        request.json.get('genre'),
        request.json.get('year'),
        product_id
    )
    cursor.execute(query, valores)
    conn.commit()
    
    cursor.close()
    conn.close()
    
    return jsonify({'id': product_id, **request.json}), 200

@app.route('/api/books/<int:product_id>', methods=['DELETE'])
def delete_product(product_id):
    conn = get_db_connection()
    if conn is None:
        return jsonify({'error': 'Error de conexión con la BD'}), 500
        
    cursor = conn.cursor()
    
    cursor.execute("SELECT * FROM productos WHERE id = %s", (product_id,))
    if cursor.fetchone() is None:
        cursor.close()
        conn.close()
        abort(404)
        
    cursor.execute("DELETE FROM productos WHERE id = %s", (product_id,))
    conn.commit()
    
    cursor.close()
    conn.close()
    return jsonify({'result': True}), 200

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)