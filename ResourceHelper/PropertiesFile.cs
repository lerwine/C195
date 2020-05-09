using System;
using System.Collections;
using System.Collections.Generic;
using System.Globalization;
using System.IO;
using System.Linq;
using System.Text;
using System.Text.RegularExpressions;
using System.Threading;


namespace ResourceHelper
{

    public class PropertiesFile
    {
        public static readonly Regex EncodeKeyRegex = new Regex(@"(?<u>[\u0000-\u0019\u007f-\uffff])|(?<c>[\b\n\t\r\f])|(?<e>[\\ ]|=(?=.))");
        public static readonly Regex EncodeValueRegex = new Regex(@"(?<u>[\u0000-\u0019\u007f-\uffff])|(?<c>[\b\n\t\r\f\\])");
        public static readonly Regex LineBreakRegex = new Regex(@"\r\n?|\n");
        public static readonly Regex StripCommentRegex = new Regex(@"^\s*# ?");
        public static readonly Regex PropertyLineRegex = new Regex(@"(?=\s*#)\s*#|(?<k>(\\.|[^=\\]+)*(=(?==))?)=(?<v>.*)$");
        public static readonly Regex UnescapeRegex = new Regex(@"\\((?<c>[bntrf])|u(?<u>[a-fA-F\d]{4})|(?<o>[0-3]([0-7][0-7]?)?|[0-7][0-7]?)|(?<l>.[^=\\]*)|$)");

        private readonly object _syncRoot = new Object();
        private string _path = "";
        private readonly Dictionary<string, string> _dictionary = new Dictionary<string, string>(StringComparer.InvariantCulture);

        public string Path { get { return _path; } }

        public void CopyFrom(PropertiesFile source, bool overwrite, bool remove)
        {
            if (null == source)
                throw new ArgumentNullException("source");
            if (ReferenceEquals(source, this))
                return;
            Monitor.Enter(_syncRoot);
            try
            {
                Monitor.Enter(source._syncRoot);
                try
                {
                    _path = source._path;
                    if (source._dictionary.Count == 0)
                    {
                        if (remove)
                            _dictionary.Clear();
                        return;
                    }
                    if (remove)
                    {
                        if (overwrite)
                        {
                            _dictionary.Clear();
                            foreach (string k in source._dictionary.Keys.ToArray())
                                _dictionary.Add(k, source._dictionary[k]);
                            return;
                        }
                        if (_dictionary.Count > 0)
                        {
                            foreach (string k in _dictionary.Keys.ToArray())
                            {
                                if (!source._dictionary.ContainsKey(k))
                                    _dictionary.Remove(k);
                            }
                        }
                    }
                    if (_dictionary.Count == 0)
                    {
                        foreach (string k in source._dictionary.Keys.ToArray())
                            _dictionary.Add(k, source._dictionary[k]);
                        return;
                    }
                    if (overwrite)
                    {
                        foreach (string k in source._dictionary.Keys)
                        {
                            if (_dictionary.ContainsKey(k))
                                _dictionary[k] = source._dictionary[k];
                            else
                                _dictionary.Add(k, source._dictionary[k]);
                        }
                    }
                    else
                    {
                        foreach (string k in source._dictionary.Keys)
                        {
                            if (!_dictionary.ContainsKey(k))
                                _dictionary.Add(k, source._dictionary[k]);
                        }
                    }
                }
                finally { Monitor.Exit(source._syncRoot); }
            }
            finally { Monitor.Exit(_syncRoot); }
        }

        public static string AssertValidFileDestination(string path, string argName)
        {
            if (null == path)
                throw new ArgumentNullException(argName);
            if ((path = BundleInfo.ToNormalizedPath(path)).Length == 0)
                throw new ArgumentException("Path is empty", argName);
            if (Directory.Exists(path) || path.Length == System.IO.Path.GetPathRoot(path).Length)
                throw new ArgumentException("Path a directory", argName);
            if (!Directory.Exists(System.IO.Path.GetDirectoryName(path)))
                throw new ArgumentException("Parent directory not found", argName);
            return path;
        }
        public static string AssertValidFileDestination(string path)
        {
            return AssertValidFileDestination(path, "path");
        }

        public static string Escape(string value, bool asKey)
        {
            if (string.IsNullOrEmpty(value))
                return "";
            if (asKey)
            {
                switch (value)
                {
                    case "=":
                        return "==";
                    case "#":
                        return "\\#";
                    case "#=":
                        return "\\#=";
                }
                bool trailing = value.EndsWith("=");
                if (trailing)
                    value = value.Substring(0, value.Length - 1);
                bool leading = value.StartsWith("#");
                if (leading)
                    value = value.Substring(1);
                value = EncodeKeyRegex.Replace(value, new MatchEvaluator((Match m) =>
                {
                    if (m.Groups["c"].Success)
                    {
                        switch (m.Groups["c"].Value)
                        {
                            case "\b":
                                return "\\b";
                            case "\t":
                                return "\\t";
                            case "\r":
                                return "\\r";
                            case "\f":
                                return "\\f";
                        }
                        return "\\n";
                    }
                    if (m.Groups['e'].Success)
                        return "\\" + m.Groups["e"].Value;
                    return "\\u" + ((int)m.Groups["u"].Value[0]).ToString("x4");
                }));

                if (leading)
                    return "\\#" + ((trailing) ? value + "=" : value);
                return (trailing) ? value + "=" : value;
            }

            return EncodeValueRegex.Replace(value, new MatchEvaluator((Match m) =>
            {
                if (m.Groups["c"].Success)
                {
                    switch (m.Groups["c"].Value)
                    {
                        case "\b":
                            return "\\b";
                        case "\t":
                            return "\\t";
                        case "\r":
                            return "\\r";
                        case "\f":
                            return "\\f";
                    }
                    return "\\n";
                }
                if (m.Groups['e'].Success)
                    return "\\" + m.Groups["e"].Value;
                return "\\u" + ((int)m.Groups["u"].Value[0]).ToString("x4");
            }));
        }
        public static string Unescape(string value, out bool hasContinuation)
        {
            if (string.IsNullOrWhiteSpace(value))
            {
                hasContinuation = false;
                return (null == value) ? "" : value;
            }
            if (value == "\\")
            {
                hasContinuation = true;
                return "";
            }
            bool c = false;
            string result = UnescapeRegex.Replace(value, new MatchEvaluator((Match m) =>
            {
                if (m.Groups["c"].Success)
                {
                    switch (m.Groups["c"].Value)
                    {
                        case "b":
                            return "\b";
                        case "t":
                            return "\t";
                        case "r":
                            return "\r";
                        case "f":
                            return "\f";
                    }
                    return "\n";
                }
                if (m.Groups["u"].Success)
                {
                    return new String(new char[] { (char)int.Parse(m.Groups["u"].Value, NumberStyles.HexNumber) });
                }
                if (m.Groups["o"].Success)
                {
                    string s = m.Groups["o"].Value;
                    if (s.Length == 1)
                        return new String(new char[] { (char)int.Parse(s) });
                    int i = int.Parse(s.Substring(0, 1)) * 7 + int.Parse(s.Substring(1, 1));
                    if (s.Length > 2)
                        return new String(new char[] { (char)(i * 8 + int.Parse(s.Substring(2))) });
                    return new String(new char[] { (char)i });
                }
                c = true;
                return "";
            }));
            hasContinuation = c;
            return result;
        }
        public void Save() { Save(null); }
        public void Save(string path)
        {
            if (null == path)
            {
                if (_path.Length == 0)
                    throw new ArgumentNullException("path");
                path = _path;
            }
            path = AssertValidFileDestination(path, "path");
            using (StreamWriter writer = new StreamWriter(path, false, new UTF8Encoding(false, false)))
            {
                Monitor.Enter(_syncRoot);
                try
                {
                    List<string> keys = new List<string>();
                    foreach (string k in _dictionary.Keys)
                        keys.Add(k);
                    keys.Sort();
                    IEnumerator<string> enumerator = keys.GetEnumerator();
                    while (enumerator.MoveNext())
                    {
                        writer.Write(Escape(enumerator.Current, true));
                        writer.Write("=");
                        writer.WriteLine(Escape(_dictionary[enumerator.Current], false));
                    }
                }
                finally { Monitor.Exit(_syncRoot); }
                writer.Flush();
            }
            _path = path;
        }
        public void Load() { Load(null); }
        public void Load(string path)
        {
            if (null == path)
            {
                if (_path.Length == 0)
                    throw new ArgumentNullException("path");
                path = _path;
            }
            path = AssertValidFileDestination(path, "path");
            Monitor.Enter(_syncRoot);
            try
            {
                Dictionary<string, string> result = new Dictionary<string, string>(StringComparer.InvariantCulture);
                if (File.Exists(path))
                {
                    string currentKey = null;
                    int lineNumber = 0;
                    bool c;
                    foreach (string line in File.ReadAllLines(path, new UTF8Encoding(false, false)))
                    {
                        if (null == currentKey)
                        {
                            Match m = PropertyLineRegex.Match(line);
                            if (!m.Success)
                            {
                                if (line.Trim().Length > 0)
                                    throw new Exception("Parse error at line " + lineNumber.ToString());
                                continue;
                            }
                            if (m.Groups["k"].Success)
                            {
                                currentKey = Unescape(m.Groups["k"].Value, out c);
                                result.Add(currentKey, Unescape(m.Groups["v"].Value, out c));
                                if (!c)
                                    currentKey = null;
                            }
                        }
                        else
                        {
                            result[currentKey] += Unescape(line, out c);
                            if (!c)
                                currentKey = null;
                        }
                    }
                }
                _dictionary.Clear();
                foreach (string k in result.Keys)
                    _dictionary.Add(k, result[k]);
            }
            finally { Monitor.Exit(_syncRoot); }
            _path = path;
        }

        public int Count { get { return _dictionary.Count; } }

        public ICollection<string> Keys { get { return _dictionary.Keys; } }

        public ICollection<string> Values { get { return _dictionary.Values; } }

        public string this[string key]
        {
            get
            {
                if (null != key)
                {
                    Monitor.Enter(_syncRoot);
                    try
                    {
                        if (_dictionary.ContainsKey(key))
                            return _dictionary[key];
                    }
                    finally { Monitor.Exit(_syncRoot); }
                }
                return null;
            }
            set
            {
                Monitor.Enter(_syncRoot);
                try
                {
                    if (_dictionary.ContainsKey(key))
                        _dictionary[key] = (null == value) ? "" : value;
                    else
                        _dictionary.Add(key, (null == value) ? "" : value);
                }
                finally { Monitor.Exit(_syncRoot); }
            }
        }

        public void Add(string key, string value)
        {
            Monitor.Enter(_syncRoot);
            try
            {
                _dictionary.Add(key, (null == value) ? "" : value);
            }
            finally { Monitor.Exit(_syncRoot); }
        }

        public void Remove(string key)
        {
            if (null != key)
            {
                Monitor.Enter(_syncRoot);
                try
                {
                    _dictionary.Remove(key);
                }
                finally { Monitor.Exit(_syncRoot); }
            }
        }

        public bool ContainsKey(string key) { return _dictionary.ContainsKey(key); }

        public Dictionary<string, string>.Enumerator GetEnumerator()
        {
            return _dictionary.GetEnumerator();
        }

    }
}