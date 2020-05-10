namespace ResourceHelper
{
    using System;
    using System.Collections.Generic;
    using System.IO;
    using System.Text;
    using System.Threading;

    public class ResourceBundle
    {
        public static string GetFileName(string baseName, LanguageCode language)
        {
            switch (language)
            {
                case LanguageCode.de:
                    return baseName + "_de.properties";
                case LanguageCode.es:
                    return baseName + "_es.properties";
                case LanguageCode.hi:
                    return baseName + "_hi.properties";
            }
            return baseName + "_en.properties";
        }

        public string BaseName { get { return _baseName; } }

        public string BasePath { get { return _basePath; } }

        public void Load() { Load(null, null); }
        public void Load(string rootPath, string relativeName)
        {
            if (null == rootPath)
            {
                rootPath = _basePath;
                string s = _baseName;
                if (rootPath.Length == 0 || s.Length == 0)
                    throw new ArgumentNullException("rootPath");
                rootPath = BundleInfo.ToNormalizedPath(rootPath.Substring(0, rootPath.Length - s.Length));
                if (null == relativeName)
                    relativeName = s;
                else if (relativeName.Trim().Length == 0)
                    throw new ArgumentException("Relative name is empty", "relativeName");
            }
            else
            {
                if ((rootPath = BundleInfo.ToNormalizedPath(rootPath)).Length == 0)
                    throw new ArgumentException("Root path is empty", "rootPath");
                if (null == relativeName)
                {
                    relativeName = _baseName;
                    if (relativeName.Length == 0)
                        throw new ArgumentNullException("rootPath");
                }
                else if (relativeName.Trim().Length == 0)
                    throw new ArgumentException("Relative name is empty", "relativeName");
            }
            string basePath = PropertiesFile.AssertValidFileDestination(Path.Combine(rootPath, relativeName), "relativeName");
            string baseName = BundleInfo.ToRelativePath(basePath, rootPath);
            string fileName = Path.GetFileName(basePath);
            string dirName = Path.GetDirectoryName(basePath);
            PropertiesFile en = new PropertiesFile(); 
            en.Load(Path.Combine(dirName, GetFileName(fileName, LanguageCode.en)));
            PropertiesFile de = new PropertiesFile();
            de.Load(Path.Combine(dirName, GetFileName(fileName, LanguageCode.de)));
            PropertiesFile es = new PropertiesFile();
            es.Load(Path.Combine(dirName, GetFileName(fileName, LanguageCode.es)));
            PropertiesFile hi = new PropertiesFile();
            hi.Load(Path.Combine(dirName, GetFileName(fileName, LanguageCode.hi)));
            Monitor.Enter(_syncRoot);
            try
            {
                _baseName = baseName;
                _basePath = basePath;
                _en.CopyFrom(en, true, true);
                _de.CopyFrom(de, true, true);
                _es.CopyFrom(es, true, true);
                _hi.CopyFrom(hi, true, true);
                foreach (string k in _en.Keys)
                {
                    if (!_de.ContainsKey(k))
                        _de.Add(k, "");
                    if (!_es.ContainsKey(k))
                        _es.Add(k, "");
                    if (!_hi.ContainsKey(k))
                        _hi.Add(k, "");
                }
                foreach (string k in _de.Keys)
                {
                    if (!_en.ContainsKey(k))
                        _en.Add(k, "");
                    if (!_es.ContainsKey(k))
                        _es.Add(k, "");
                    if (!_hi.ContainsKey(k))
                        _hi.Add(k, "");
                }
                foreach (string k in _es.Keys)
                {
                    if (!_en.ContainsKey(k))
                        _en.Add(k, "");
                    if (!_de.ContainsKey(k))
                        _de.Add(k, "");
                    if (!_hi.ContainsKey(k))
                        _hi.Add(k, "");
                }
                foreach (string k in _hi.Keys)
                {
                    if (!_en.ContainsKey(k))
                        _en.Add(k, "");
                    if (!_de.ContainsKey(k))
                        _de.Add(k, "");
                    if (!_es.ContainsKey(k))
                        _es.Add(k, "");
                }
            }
            finally { Monitor.Exit(_syncRoot); }
        }

        public void Save() { Save(null, null); }
        public void Save(string rootPath, string relativeName)
        {
            if (null == rootPath)
            {
                rootPath = _basePath;
                string s = _baseName;
                if (rootPath.Length == 0 || s.Length == 0)
                    throw new ArgumentNullException("rootPath");
                rootPath = BundleInfo.ToNormalizedPath(rootPath.Substring(0, rootPath.Length - s.Length));
                if (null == relativeName)
                    relativeName = s;
                else if (relativeName.Trim().Length == 0)
                    throw new ArgumentException("Relative name is empty", "relativeName");
            }
            else
            {
                if ((rootPath = BundleInfo.ToNormalizedPath(rootPath)).Length == 0)
                    throw new ArgumentException("Root path is empty", "rootPath");
                if (null == relativeName)
                {
                    relativeName = _baseName;
                    if (relativeName.Length == 0)
                        throw new ArgumentNullException("rootPath");
                }
                else if (relativeName.Trim().Length == 0)
                    throw new ArgumentException("Relative name is empty", "relativeName");
            }
            string basePath = PropertiesFile.AssertValidFileDestination(Path.Combine(rootPath, relativeName), "relativeName");
            string baseName = BundleInfo.ToRelativePath(basePath, rootPath);
            string fileName = Path.GetFileName(basePath);
            string dirName = Path.GetDirectoryName(basePath);
            Monitor.Enter(_syncRoot);
            try
            {
                PropertiesFile en = new PropertiesFile();
                en.CopyFrom(_en, true, true);
                PropertiesFile de = new PropertiesFile();
                de.CopyFrom(_de, true, true);
                PropertiesFile es = new PropertiesFile();
                es.CopyFrom(_es, true, true);
                PropertiesFile hi = new PropertiesFile();
                hi.CopyFrom(_hi, true, true);

                en.Save(Path.Combine(dirName, GetFileName(fileName, LanguageCode.en)));
                de.Save(Path.Combine(dirName, GetFileName(fileName, LanguageCode.de)));
                es.Save(Path.Combine(dirName, GetFileName(fileName, LanguageCode.es)));
                hi.Save(Path.Combine(dirName, GetFileName(fileName, LanguageCode.hi)));
                _baseName = baseName;
                _basePath = basePath;
                _en.CopyFrom(en, false, false);
                _de.CopyFrom(de, false, false);
                _es.CopyFrom(es, false, false);
                _hi.CopyFrom(hi, false, false);
            }
            finally { Monitor.Exit(_syncRoot); }
        }

        public void SaveCode(string rootDirectory) { SaveCode(rootDirectory, null); }

        public void SaveCode(string rootDirectory, string relativeBasePath)
        {
            if (null == rootDirectory)
                throw new ArgumentNullException("rootDirectory");
            rootDirectory = BundleInfo.ToNormalizedPath(rootDirectory);
            if (rootDirectory.Length == 0)
                throw new ArgumentException("Root directory is empty", "rootDirectory");
            if (null == relativeBasePath)
            {
                relativeBasePath = _baseName;
                if (relativeBasePath.Length == 0)
                    throw new ArgumentNullException("relativeBasePath");
            }
            else if (relativeBasePath.Trim().Length == 0)
                throw new ArgumentException("Relative base path is empty", "relativeBasePath");

            string basePath = PropertiesFile.AssertValidFileDestination(Path.Combine(rootDirectory, relativeBasePath), "relativeBasePath");
            string baseName = BundleInfo.ToRelativePath(basePath, rootDirectory);
            string fileName = Path.GetFileName(basePath);
            string dirName = Path.GetDirectoryName(basePath);

            List<string> elements = new List<string>();
            string p = baseName;
            do
            {
                elements.Add(Path.GetFileName(p));
                p = Path.GetDirectoryName(p);
            } while (null != p && p.Length > 0 && p != "." && p != "..");
            string className = elements[0];
            elements.RemoveAt(0);
            if (elements.Count > 1)
                elements.Reverse();

            using (StreamWriter writer = new StreamWriter(Path.Combine(dirName, fileName + "ResourceKeys.java"), false, new UTF8Encoding(false, false)))
            {
                writer.Write("package ");
                IEnumerator<string> enumerator = elements.GetEnumerator();
                enumerator.MoveNext();
                writer.Write(enumerator.Current);
                while (enumerator.MoveNext())
                {
                    writer.Write(".");
                    writer.Write(enumerator.Current);
                }
                writer.WriteLine(";");
                writer.WriteLine("");
                writer.WriteLine("/**");
                writer.Write(" * Defines resource bundle keys for the {@code ");
                writer.Write(baseName.Replace("\\", "/"));
                writer.WriteLine("} resource bundle.");
                writer.WriteLine(" *");
                writer.WriteLine(" * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;");
                writer.WriteLine(" */");
                writer.Write("public interface ");
                writer.Write(className);
                writer.WriteLine("ResourceKeys {");

                List<string> keys = new List<string>();
                foreach (string k in _current.Keys)
                    keys.Add(k);
                keys.Sort();
                enumerator = keys.GetEnumerator();
                while (enumerator.MoveNext())
                {
                    writer.WriteLine("");
                    writer.WriteLine("    /**");
                    writer.Write("     * Resource key in the current {@link java.util.ResourceBundle} that contains the locale-specific text for {@code \"");
                    writer.Write(PropertiesFile.Escape(_current[enumerator.Current], false).Replace("\"", "\\\""));
                    writer.WriteLine("\"}.");
                    writer.WriteLine("     */");
                    writer.Write("    public static final String RESOURCEKEY_");
                    writer.Write(enumerator.Current.ToUpper());
                    writer.Write(" = \"");
                    writer.Write(enumerator.Current);
                    writer.WriteLine("\";");
                }
                writer.WriteLine("");
                writer.WriteLine("}");
                writer.Flush();
            }
        }

        private readonly object _syncRoot = new Object();
        private string _baseName = "";
        private string _basePath = "";
        private readonly PropertiesFile _en = new PropertiesFile();
        private readonly PropertiesFile _de = new PropertiesFile();
        private readonly PropertiesFile _es = new PropertiesFile();
        private readonly PropertiesFile _hi = new PropertiesFile();
        private PropertiesFile _current;
        private LanguageCode _language = LanguageCode.en;

        public ResourceBundle()
        {
            _current = _en;
        }

        public int Count { get { return _current.Count; } }

        public ICollection<string> Keys { get { return _current.Keys; } }

        public ICollection<string> Values { get { return _current.Values; } }

        public void CopyTo(string key, ResourceBundle other, bool force)
        {
            if (null == key)
                throw new ArgumentNullException();
            Monitor.Enter(_syncRoot);
            try
            {
                Monitor.Enter(other._syncRoot);
                try
                {
                    if (_current.ContainsKey(key))
                    {
                        if (other._current.ContainsKey(key))
                        {
                            other._en[key] = _en[key];
                            other._de[key] = _de[key];
                            other._es[key] = _es[key];
                            other._hi[key] = _hi[key];
                        }
                        else
                        {
                            other._en.Add(key, _en[key]);
                            other._de.Add(key, _de[key]);
                            other._es.Add(key, _es[key]);
                            other._hi.Add(key, _hi[key]);
                        }
                    }
                    else if (force)
                    {
                        if (other._current.ContainsKey(key))
                        {
                            other._en[key] = "";
                            other._de[key] = "";
                            other._es[key] = "";
                            other._hi[key] = "";
                        }
                        else
                        {
                            other._en.Add(key, "");
                            other._de.Add(key, "");
                            other._es.Add(key, "");
                            other._hi.Add(key, "");
                        }
                    }
                }
                finally { Monitor.Exit(other._syncRoot); }
            }
            finally { Monitor.Exit(_syncRoot); }
        }

        public void MoveTo(string key, ResourceBundle other, bool force)
        {
            if (null == key)
                throw new ArgumentNullException();
            Monitor.Enter(_syncRoot);
            try
            {
                Monitor.Enter(other._syncRoot);
                try
                {
                    if (_current.ContainsKey(key))
                    {
                        if (other._current.ContainsKey(key))
                        {
                            other._en[key] = _en[key];
                            other._de[key] = _de[key];
                            other._es[key] = _es[key];
                            other._hi[key] = _hi[key];
                        }
                        else
                        {
                            other._en.Add(key, _en[key]);
                            other._de.Add(key, _de[key]);
                            other._es.Add(key, _es[key]);
                            other._hi.Add(key, _hi[key]);
                        }
                        _en.Remove(key);
                        _de.Remove(key);
                        _es.Remove(key);
                        _hi.Remove(key);
                    }
                    else if (force)
                    {
                        if (other._current.ContainsKey(key))
                        {
                            other._en[key] = "";
                            other._de[key] = "";
                            other._es[key] = "";
                            other._hi[key] = "";
                        }
                        else
                        {
                            other._en.Add(key, "");
                            other._de.Add(key, "");
                            other._es.Add(key, "");
                            other._hi.Add(key, "");
                        }
                    }
                }
                finally { Monitor.Exit(other._syncRoot); }
            }
            finally { Monitor.Exit(_syncRoot); }
        }

        public LanguageCode Language
        {
            get
            {
                return _language;
            }
            set
            {
                Monitor.Enter(_syncRoot);
                try
                {
                    _language = value;
                    switch (value)
                    {
                        case LanguageCode.de:
                            _current = _de;
                            break;
                        case LanguageCode.es:
                            _current = _es;
                            break;
                        case LanguageCode.hi:
                            _current = _hi;
                            break;
                        default:
                            _current = _en;
                            break;
                    }
                }
                finally { Monitor.Exit(_syncRoot); }
            }
        }

        public string this[string key]
        {
            get
            {
                Monitor.Enter(_syncRoot);
                try { return (key != null && _current.ContainsKey(key)) ? _current[key] : null; }
                finally { Monitor.Exit(_syncRoot); }
            }
            set
            {
                if (null == key)
                    throw new ArgumentNullException();
                Monitor.Enter(_syncRoot);
                try
                {
                    if (_current.ContainsKey(key))
                    {
                        _current[key] = (null == value) ? "" : value;
                    }
                    else
                    {
                        _current.Add(key, (null == value) ? "" : value);
                        switch (_language)
                        {
                            case LanguageCode.de:
                                _en.Add(key, "");
                                _es.Add(key, "");
                                _hi.Add(key, "");
                                break;
                            case LanguageCode.es:
                                _en.Add(key, "");
                                _de.Add(key, "");
                                _hi.Add(key, "");
                                break;
                            case LanguageCode.hi:
                                _en.Add(key, "");
                                _de.Add(key, "");
                                _es.Add(key, "");
                                break;
                            default:
                                _hi.Add(key, "");
                                _de.Add(key, "");
                                _es.Add(key, "");
                                break;
                        }
                    }
                }
                finally { Monitor.Exit(_syncRoot); }
            }
        }
        public string Get(string key, LanguageCode language)
        {
            Monitor.Enter(_syncRoot);
            try
            {
                if (key != null && _current.ContainsKey(key))
                {
                    switch (language)
                    {
                        case LanguageCode.de:
                            return _de[key];
                        case LanguageCode.es:
                            return _es[key];
                        case LanguageCode.hi:
                            return _hi[key];
                        default:
                            return _en[key];
                    }
                }
            }
            finally { Monitor.Exit(_syncRoot); }
            return null;
        }
        public ICollection<string> GetValues(LanguageCode language)
        {
            switch (language)
            {
                case LanguageCode.de:
                    return _de.Values;
                case LanguageCode.es:
                    return _es.Values;
                case LanguageCode.hi:
                    return _hi.Values;
                default:
                    return _en.Values;
            }
        }
        public string GetPath(LanguageCode language)
        {
            switch (language)
            {
                case LanguageCode.de:
                    return _de.Path;
                case LanguageCode.es:
                    return _es.Path;
                case LanguageCode.hi:
                    return _hi.Path;
                default:
                    return _en.Path;
            }
        }
        public void Set(string key, string value, LanguageCode language)
        {
            Monitor.Enter(_syncRoot);
            try
            {
                if (key != null && _current.ContainsKey(key))
                {
                    switch (language)
                    {
                        case LanguageCode.de:
                            _de[key] = (null == value) ? "" : value;
                            break;
                        case LanguageCode.es:
                            _es[key] = (null == value) ? "" : value;
                            break;
                        case LanguageCode.hi:
                            _hi[key] = (null == value) ? "" : value;
                            break;
                        default:
                            _en[key] = (null == value) ? "" : value;
                            break;
                    }
                }
                else
                {
                    switch (language)
                    {
                        case LanguageCode.de:
                            _en.Add(key, "");
                            _de.Add(key, (null == value) ? "" : value);
                            _es.Add(key, "");
                            _hi.Add(key, "");
                            break;
                        case LanguageCode.es:
                            _en.Add(key, "");
                            _de.Add(key, "");
                            _es.Add(key, (null == value) ? "" : value);
                            _hi.Add(key, "");
                            break;
                        case LanguageCode.hi:
                            _en.Add(key, "");
                            _de.Add(key, "");
                            _es.Add(key, "");
                            _hi.Add(key, (null == value) ? "" : value);
                            break;
                        default:
                            _en.Add(key, (null == value) ? "" : value);
                            _de.Add(key, "");
                            _es.Add(key, "");
                            _hi.Add(key, "");
                            break;
                    }
                }
            }
            finally { Monitor.Exit(_syncRoot); }
        }

        public Dictionary<string, string>.Enumerator GetEnumerator()
        {
            return _current.GetEnumerator();
        }

        public Dictionary<string, string>.Enumerator GetEnumerator(LanguageCode language)
        {
            switch (language)
            {
                case LanguageCode.de:
                    return _de.GetEnumerator();
                case LanguageCode.es:
                    return _es.GetEnumerator();
                case LanguageCode.hi:
                    return _hi.GetEnumerator();
                default:
                    return _en.GetEnumerator();
            }
        }

        public bool ContainsKey(string key)
        {
            return null != key && _current.ContainsKey(key);
        }

        public void Remove(string key)
        {
            if (key != null)
            {
                Monitor.Enter(_syncRoot);
                try
                {
                    if (_current.ContainsKey(key))
                    {
                        _en.Remove(key);
                        _de.Remove(key);
                        _es.Remove(key);
                        _hi.Remove(key);
                    }
                }
                finally { Monitor.Exit(_syncRoot); }
            }
        }

    }
}