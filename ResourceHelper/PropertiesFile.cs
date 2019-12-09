namespace ResourceHelper
{
    using System;
    using System.Collections;
    using System.Collections.Generic;
    using System.Collections.ObjectModel;
    using System.Globalization;
    using System.IO;
    using System.Linq;
    using System.Text;
    using System.Text.RegularExpressions;

    public class PropertiesFile : Collection<PropertiesFile.Node>, IDictionary<string, IList<string>>, IDictionary, ICollection<IList<string>>
    {
        public static readonly Regex NewLine = new Regex(@"\r\n?|\n");
        public static readonly Regex Escaped = new Regex(@"\\u(?<h>[\da-f]{4})|\\.?");
        public static readonly Regex ShouldEscape = new Regex(@"\\|[\u0000-\u0008\u000a-\u001f\u007f-\uffff]");
        private Dictionary<string, int> _dictionary = new Dictionary<string, int>(StringComparer.InvariantCultureIgnoreCase);
        public IList<string> this[string key]
        {
            get
            {
                if (_dictionary.ContainsKey(key))
                    return this[_dictionary[key]];
                return null;
            }
            set
            {
                Node node = (value == null) ? new Node(key, "") : ((value is Node) ? (Node)value : new Node(key, value));
                if (_dictionary.ContainsKey(node.Key))
                    this[_dictionary[key]] = node;
                else
                    Add(node);
            }
        }

        object IDictionary.this[object key] { get { return this[(string)key]; } set { this[(string)key] = (Node)value; } }

        public ICollection<string> Keys { get { return _dictionary.Keys; } }

        ICollection IDictionary.Keys { get { return _dictionary.Keys; } }

        ICollection<IList<string>> IDictionary<string, IList<string>>.Values { get { return this; } }

        ICollection IDictionary.Values { get { return this; } }

        bool ICollection<IList<string>>.IsReadOnly { get { return ((ICollection<string>)this).IsReadOnly; } }

        bool ICollection<KeyValuePair<string, IList<string>>>.IsReadOnly { get { return ((ICollection<string>)this).IsReadOnly; } }

        bool IDictionary.IsReadOnly { get { return ((ICollection<string>)this).IsReadOnly; } }

        bool IDictionary.IsFixedSize { get { return ((IList)this).IsFixedSize; } }

        public PropertiesFile AsSorted()
        {
            if (Count < 2)
                return this;
            PropertiesFile result = new PropertiesFile();
            foreach (Node n in Items.OrderBy(i => i.Key, StringComparer.InvariantCultureIgnoreCase))
                result.Add(n);
            return result;
        }

        public static string Unescape(string source, out bool hasContinuation)
        {
            if (string.IsNullOrEmpty(source))
            {
                hasContinuation = false;
                return "";
            }
            MatchCollection mc = Escaped.Matches(source);
            if (mc.Count == 0)
            {
                hasContinuation = false;
                return source;
            }
            hasContinuation = mc[mc.Count - 1].Length == 1;
            StringBuilder sb = new StringBuilder();
            int index = 0;
            foreach (Match m in mc)
            {
                if (index < m.Index)
                    sb.Append(source.Substring(index, m.Index - index));
                index = m.Index + m.Length;
                if (m.Groups["h"].Success)
                    sb.Append(Convert.ToChar(int.Parse(m.Groups[1].Value, NumberStyles.HexNumber)));
                else if (m.Length > 1)
                    sb.Append(m.Value.Substring(1));
            }
            if (index < source.Length)
                sb.Append(source.Substring(index));
            return sb.ToString();
        }

        public static string Escape(string source)
        {
            if (string.IsNullOrEmpty(source))
                return "";
            return ShouldEscape.Replace(source, (Match m) => {
                if (m.Value == "\\")
                    return "\\\\";
                return "\\u" + Convert.ToInt32(m.Value[0]).ToString("x4");
            });
        }

        public static PropertiesFile Load(string path)
        {
            PropertiesFile result = new PropertiesFile();
            Node coll = null;
            bool hasContinuation = false;
            int lineNumber = 0;
            foreach (string source in File.ReadAllLines(path))
            {
                lineNumber++;
                if (hasContinuation)
                {
                    coll.Add(Unescape(source, out hasContinuation));
                    continue;
                }
                if (source.Trim().Length == 0 || source[0] == '#')
                    continue;
                int index = source.IndexOf('=');
                if (index < 1)
                    throw new Exception("Invalid key/value sequence at line " + lineNumber.ToString());
                string key = source.Substring(0, index);
                if (result._dictionary.ContainsKey(key))
                    throw new Exception("Duplicate key at line " + lineNumber.ToString());
                coll = new Node(key, (index < source.Length - 1) ? source.Substring(index + 1) : "");
                result.Add(coll);
            }

            return result;
        }

        public void Save(string path)
        {
            using (StreamWriter writer = new StreamWriter(path, false, new UTF8Encoding(false, false)))
            {
                Save(writer);
                writer.Flush();
            }
        }

        public void Save(TextWriter writer)
        {
            foreach (Node n in Items)
                n.WriteTo(writer);
            writer.Flush();
        }

        protected override void ClearItems()
        {
            base.ClearItems();
            _dictionary.Clear();
        }

        protected override void InsertItem(int index, Node item)
        {
            if (item == null)
                throw new ArgumentNullException("item");
            if (_dictionary.ContainsKey(item.Key))
                throw new ArgumentException("Another node with that key already exists.", "item");
            foreach (string key in _dictionary.Keys.Where(k => _dictionary[k] >= index).ToArray())
                _dictionary[key]++;
            _dictionary.Add(item.Key, index);
            base.InsertItem(index, item);
        }

        protected override void RemoveItem(int index)
        {
            Node item = this[index];
            _dictionary.Remove(item.Key);
            foreach (string key in _dictionary.Keys.Where(k => _dictionary[k] > index).ToArray())
                _dictionary[key]--;
            base.RemoveItem(index);
        }

        protected override void SetItem(int index, Node item)
        {
            if (item == null)
                throw new ArgumentNullException("item");
            if (_dictionary.ContainsKey(item.Key))
            {
                if (_dictionary[item.Key] != index)
                    throw new ArgumentException("Another node with that key already exists.", "item");
            }
            else
            {
                Node oldItem = this[index];
                _dictionary.Remove(oldItem.Key);
                _dictionary.Add(item.Key, index);
            }
            base.SetItem(index, item);
        }

        public void Add(string key, IList<string> value)
        {
            Add((value == null) ? new Node(key, value) : ((value is Node && ((Node)value).Key == key) ? (Node)value : new Node(key, value)));
        }

        void IDictionary.Add(object key, object value)
        {
            if (value == null)
                Add(new Node((string)key, ""));
            else if (value is string)
                Add(new Node((string)key, (string)value));
            else
                Add((string)key, (IList<string>)value);
        }

        void ICollection<KeyValuePair<string, IList<string>>>.Add(KeyValuePair<string, IList<string>> item)
        {
            Add(item.Key, item.Value);
        }

        public bool ContainsKey(string key) { return _dictionary.ContainsKey(key); }

        bool ICollection<KeyValuePair<string, IList<string>>>.Contains(KeyValuePair<string, IList<string>> item)
        {
            return _dictionary.ContainsKey(item.Key) && this[_dictionary[item.Key]].Equals(item.Value);
        }

        bool IDictionary.Contains(object key)
        {
            return key is string && ContainsKey((string)key);
        }

        void ICollection<KeyValuePair<string, IList<string>>>.CopyTo(KeyValuePair<string, IList<string>>[] array, int arrayIndex)
        {
            Items.Select(i => new KeyValuePair<string, IList<string>>(i.Key, i)).ToList().CopyTo(array, arrayIndex);
        }

        IEnumerator<IList<string>> IEnumerable<IList<string>>.GetEnumerator() { return new DictionaryEnumerator(Items); }

        IEnumerator<KeyValuePair<string, IList<string>>> IEnumerable<KeyValuePair<string, IList<string>>>.GetEnumerator() { return new DictionaryEnumerator(Items); }

        IDictionaryEnumerator IDictionary.GetEnumerator() { return new DictionaryEnumerator(Items); }

        public bool Remove(string key)
        {
            if (_dictionary.ContainsKey(key))
            {
                RemoveAt(_dictionary[key]);
                return true;
            }
            return false;
        }

        bool ICollection<KeyValuePair<string, IList<string>>>.Remove(KeyValuePair<string, IList<string>> item)
        {
            if (_dictionary.ContainsKey(item.Key) && _dictionary[item.Key].Equals(item.Value))
            {
                RemoveAt(_dictionary[item.Key]);
                return true;
            }
            return false;
        }

        void IDictionary.Remove(object key)
        {
            if (key is string)
                Remove((string)key);
        }

        public bool TryGetValue(string key, out IList<string> value)
        {
            int index;
            if (_dictionary.TryGetValue(key, out index))
            {
                value = this[index];
                return true;
            }
            value = null;
            return false;
        }

        void ICollection<IList<string>>.Add(IList<string> item) { Add((Node)item); }

        bool ICollection<IList<string>>.Contains(IList<string> item) { return Items.Any(i => i.Equals(item)); }

        void ICollection<IList<string>>.CopyTo(IList<string>[] array, int arrayIndex)
        {
            Items.Cast<IList<string>>().ToList().CopyTo(array, arrayIndex);
        }

        bool ICollection<IList<string>>.Remove(IList<string> item)
        {
            for (int i = 0; i < Count; i++)
            {
                if (Items[i].Equals(item))
                {
                    RemoveAt(i);
                    return true;
                }
            }
            return false;
        }

        public class DictionaryEnumerator : IEnumerator<KeyValuePair<string, IList<string>>>, IEnumerator<IList<string>>, IDictionaryEnumerator
        {
            private IEnumerator<Node> _innerEnumerator;
            private KeyValuePair<string, IList<string>>? _current = null;
            private DictionaryEntry? _entry = null;

            public KeyValuePair<string, IList<string>> Current
            {
                get
                {
                    if (_current.HasValue)
                        return _current.Value;
                    _current = new KeyValuePair<string, IList<string>>(_innerEnumerator.Current.Key, _innerEnumerator.Current);
                    return _current.Value;
                }
            }

            public string Key { get { return _innerEnumerator.Current.Key; } }

            object IDictionaryEnumerator.Key { get { return _innerEnumerator.Current.Key; } }

            public Node Value { get { return _innerEnumerator.Current; } }

            object IDictionaryEnumerator.Value { get { return _innerEnumerator.Current; } }

            public DictionaryEntry Entry
            {
                get
                {
                    if (_entry.HasValue)
                        return _entry.Value;
                    _entry = new DictionaryEntry(_innerEnumerator.Current.Key, _innerEnumerator.Current);
                    return _entry.Value;
                }
            }

            object IEnumerator.Current { get { return _innerEnumerator.Current; } }

            IList<string> IEnumerator<IList<string>>.Current { get { return _innerEnumerator.Current; } }

            public DictionaryEnumerator(IEnumerable<Node> nodes) { _innerEnumerator = nodes.GetEnumerator(); }

            public void Dispose() { _innerEnumerator.Dispose(); }

            public bool MoveNext()
            {
                if (_innerEnumerator.MoveNext())
                {
                    _current = null;
                    _entry = null;
                    return true;
                }
                return false;
            }

            public void Reset()
            {
                _innerEnumerator.Reset();
                _current = null;
                _entry = null;
            }
        }

        public class Node : IList<string>, IList, IEquatable<IList<string>>
        {
            private string _key;

            public string Key { get { return _key; } }

            private List<string> _innerList = new List<string>();

            public string this[int index]
            {
                get { return _innerList[index]; }
                set { _innerList[index] = (value == null) ? "" : value; }
            }

            object IList.this[int index]
            {
                get { return _innerList[index]; }
                set { this[index] = (string)value; }
            }

            public int Count { get { return _innerList.Count; } }

            bool ICollection<string>.IsReadOnly { get { return ((ICollection<string>)_innerList).IsReadOnly; } }

            bool IList.IsReadOnly { get { return ((IList)_innerList).IsReadOnly; } }

            bool IList.IsFixedSize { get { return ((IList)_innerList).IsFixedSize; } }

            object ICollection.SyncRoot { get { return ((ICollection)_innerList).SyncRoot; } }

            bool ICollection.IsSynchronized { get { return ((ICollection)_innerList).IsSynchronized; } }

            public Node(string key, string value)
            {
                _key = (key == null) ? "" : key;
                _innerList = new List<string>();
                _innerList.Add((value == null) ? "" : value);
            }

            public Node(string key, IEnumerable<string> collection)
            {
                _innerList = (collection == null) ? new List<string>() : new List<string>(collection.Select(s => (s == null) ? "" : s));
            }

            public void Add(string item) { _innerList.Add((item == null) ? "" : item); }
            int IList.Add(object value) { return ((IList)_innerList).Add((value == null) ? "" : value); }
            public void Clear() { _innerList.Clear(); }
            public bool Contains(string item) { return _innerList.Contains(item); }
            bool IList.Contains(object value) { return ((IList)_innerList).Contains(value); }
            public void CopyTo(string[] array, int arrayIndex) { _innerList.CopyTo(array, arrayIndex); }
            void ICollection.CopyTo(Array array, int index) { _innerList.ToArray().CopyTo(array, index); }
            public IEnumerator<string> GetEnumerator() { return _innerList.GetEnumerator(); }
            IEnumerator IEnumerable.GetEnumerator() { return _innerList.GetEnumerator(); }
            public int IndexOf(string item) { return _innerList.IndexOf(item); }
            int IList.IndexOf(object value) { return ((IList)_innerList).IndexOf(value); }
            public void Insert(int index, string item) { _innerList.Insert(index, (item == null) ? "" : item); }
            void IList.Insert(int index, object value) { ((IList)_innerList).Insert(index, (value == null) ? "" : value); }
            public bool Remove(string item) { return _innerList.Remove(item); }
            void IList.Remove(object value) { ((IList)_innerList).Remove(value); }
            public void RemoveAt(int index) { _innerList.RemoveAt(index); }

            public bool Equals(IList<string> other)
            {
                if (other == null)
                    return false;
                if (ReferenceEquals(this, other))
                    return true;
                if (_innerList.Count != other.Count)
                    return false;
                using (IEnumerator<string> e1 = _innerList.GetEnumerator())
                {
                    using (IEnumerator<string> e2 = other.GetEnumerator())
                    {
                        while (e1.MoveNext())
                        {
                            if (!e2.MoveNext() || e2.Current == null || e2.Current != e1.Current)
                                return false;
                        }
                        if (e2.MoveNext())
                            return false;
                    }
                }
                return true;
            }

            public override bool Equals(object obj)
            {
                return base.Equals(obj);
            }

            public override int GetHashCode() { return StringComparer.InvariantCultureIgnoreCase.GetHashCode(Key); }

            public override string ToString()
            {
                using (StringWriter writer = new StringWriter())
                {
                    WriteTo(writer);
                    return writer.ToString();
                }
            }

            public void WriteTo(TextWriter writer)
            {
                writer.Write(Key);
                writer.Write("=");
                string[] values = _innerList.ToArray();
                int e = values.Length - 1;
                if (e < 0)
                    writer.WriteLine();
                else
                {
                    for (int i = 0; i < e; i++)
                    {
                        writer.Write(Escape(values[i]));
                        writer.WriteLine("\\");
                    }
                    writer.WriteLine(Escape(values[e]));
                }
            }
        }
    }
}