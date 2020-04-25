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
    using System.Threading;

    public enum LanguageCode
    {
        en = 0,
        de = 1,
        es = 2,
        hi = 3
    }

    public abstract class PackageItemUrl
    {
        private readonly Uri _root;
        private readonly string _package;

        public Uri Root { get { return _root; } }

        public String Package { get { return _package; } }

        public abstract bool IsPackage();

        public PackageItemUrl(Uri root, string packageName)
        {
            if (null == root)
                throw new ArgumentNullException("root", "Directory root cannot be null");
            if (!root.IsAbsoluteUri)
                throw new ArgumentNullException("root", "Directory root cannot be null");
            _root = AssertValidAbsolutePackageUri(root, "root");
            if (string.IsNullOrEmpty(packageName))
                _package = "/";
            else
            {
                Uri uri = new Uri(_root, packageName);
                packageName = uri.ToString();
                if (packageName.Contains("?"))
                    throw new ArgumentException("Path name cannot contain a query string", "packageName");
                if (packageName.Contains("#"))
                    throw new ArgumentException("Path name cannot contain a fragment", "packageName");
                if (!_root.IsBaseOf(uri))
                    throw new ArgumentException("Package name is outside of the root directory", "packageName");
                _package = _root.MakeRelativeUri(uri).ToString();
            }
        }

        protected PackageItemUrl(PackageItemUrl url) {
            if (null == url)
                throw new ArgumentNullException();
            _root = url._root;
            _package = url._package;
        }
        
        public override int GetHashCode()
        {
            return ToString().GetHashCode();
        }

        public Uri GetFullPackageUri()
        {
            return (_package.Length == 1) ? _root : new Uri(_root, _package);
        }

        public abstract Uri GetFullResourceUri();

        public override string ToString()
        {
            return GetFullResourceUri().LocalPath;
        }

        public static Uri AssertValidAbsolutePackageUri(Uri uri, string argName)
        {
            string path = uri.ToString();
            if (path.Contains("?"))
                throw new ArgumentException("Path name cannot contain a query string", "path");
            if (path.Contains("#"))
                throw new ArgumentException("Path name cannot contain a fragment", "path");
            if (uri.Scheme != Uri.UriSchemeFile)
                throw new ArgumentException("Path is not a file system path", "path");
            if (path[path.Length - 1] == '/')
                return uri;
            return new Uri(path + "\"", UriKind.Absolute);
        }

        public static Uri AssertValidAbsolutePackageUri(Uri uri)
        {
            return AssertValidAbsolutePackageUri(uri, "uri");
        }

        public static Uri ToAbsolutePackageUri(string path)
        {
            if (null == path)
                throw new ArgumentNullException("path", "Path cannot be null");
            if (path.Trim().Length == 0)
                throw new ArgumentException("Path name cannot be empty", "path");
            Uri uri;
            if (Uri.TryCreate(path, UriKind.Absolute, out uri))
                return AssertValidAbsolutePackageUri(uri, "path");

            if (Uri.TryCreate(path, UriKind.Relative, out uri))
                throw new ArgumentException("Path must be absolute", "path");
            throw new ArgumentException("Invalid path", "path");
        }

        private static Uri AssertValidAbsoluteFileUri(Uri uri, string argName)
        {
            string path = uri.ToString();
            if (path.Contains("?"))
                throw new ArgumentException("Path name cannot contain a query string", argName);
            if (path.Contains("#"))
                throw new ArgumentException("Path name cannot contain a fragment", argName);
            if (uri.Scheme != Uri.UriSchemeFile)
                throw new ArgumentException("Path is not a file system path", argName);
            while (path[path.Length - 1] == '/')
                uri = new Uri(path.Substring(0, path.Length - 1), UriKind.Absolute);
            FileInfo f = new FileInfo(uri.LocalPath);
            while (f.Directory != null)
            {
                if (f.Name.Length > 0)
                    return uri;
                f = new FileInfo(f.DirectoryName);
            }
            throw new ArgumentException("Path cannot be a filsystem root", argName);
        }

        public static Uri AssertValidAbsoluteFileUri(Uri uri)
        {
            if (null == uri)
                throw new ArgumentNullException("path", "Path cannot be null");
            if (!uri.IsAbsoluteUri)
                throw new ArgumentException("URL must be absolute", "path");
            return AssertValidAbsoluteFileUri(uri, "path");
        }

        public static Uri ToAbsoluteFileUri(string path)
        {
            if (null == path)
                throw new ArgumentNullException("path", "Path cannot be null");
            if (path.Trim().Length == 0)
                throw new ArgumentException("Path name cannot be empty", "path");
            Uri uri;
            if (Uri.TryCreate(path, UriKind.Absolute, out uri))
                return AssertValidAbsoluteFileUri(uri, "path");

            if (Uri.TryCreate(path, UriKind.Relative, out uri))
                throw new ArgumentException("Path must be absolute", "path");
            throw new ArgumentException("Invalid path", "path");
        }

    }

    public class ResourceUrl : PackageItemUrl, IEquatable<ResourceUrl>
    {
        private readonly string _fileName;

        public String Name { get { return _fileName; } }

        public override bool IsPackage()
        {
            return null == _fileName;
        }

        //internal ResourceUrl(ResourceUrl url, LanguageCode? code)
        internal ResourceUrl(ResourceUrl url) : base(url)
        {
            _fileName = url._fileName;
        }

        public ResourceUrl(Uri root, string packageName, string fileName) : base(root, packageName)
        {
            if (string.IsNullOrEmpty(fileName)) {
                _fileName = "";
                return;
            }
            Uri uri = AssertValidAbsoluteFileUri(new Uri(GetFullPackageUri(), fileName));
            if (root.IsBaseOf(uri)) {
                fileName = root.MakeRelativeUri(uri).ToString();
                if (!fileName.Contains("/")) {
                    _fileName = fileName;
                    return;
                }
            }
            throw new ArgumentException("File name must be a leaf");
        }

        public bool Equals(ResourceUrl other)
        {
            return null != other && (ReferenceEquals(this, other) ||
                (string.Equals(_package, other._package, StringComparison.InvariantCultureIgnoreCase) &&
                ((null == _name) ? (null == other._name) :
                (null != other._name && string.Equals(_name, other._name, StringComparison.InvariantCultureIgnoreCase) &&
                _code.Value == other._code.Value))));
        }

        public override bool Equals(object obj)
        {
            return null != obj && obj is ResourceUrl && Equals((ResourceUrl)obj);
        }

        public override Uri GetFullResourceUri()
        {
            if (_fileName == null)
                return GetFullPackageUri();
            return new Uri(GetFullPackageUri(), _fileName);
        }
    }

    public class BundleInfo
    {
        public static readonly Regex LanguageCodeRegex = new Regex(@"(?<b>.+?)_(?<c>en|de|es|hi)(.properties)?$", RegexOptions.Compiled | RegexOptions.IgnoreCase | RegexOptions.CultureInvariant);
        private readonly string _packageUri;
        private readonly string _name;
        private readonly BundleFileUrl[] _files;

        public BundleFileUrl this[LanguageCode key] { get { return _files[(int)key]; } }
        
        public string PackageUri { get { return _packageUri; } }

        public string Name { get { return _name; } }

        public string ToBaseName() { return (_packageUri.Length > 0) ? _packageUri + "/" + _name : _name; }

        public static Collection<BundleInfo> Create(String rootDirectory)
        {
            if (null == rootDirectory)
                throw new ArgumentNullException("rootDirectory");
            if ((rootDirectory = ToNormalizedPath(rootDirectory)).Length == 0)
                throw new ArgumentException("Root directory is empty", "rootDirectory");
            if (!Directory.Exists(rootDirectory))
                throw new DirectoryNotFoundException();
            Uri rootUri = ResourceUrl.ToAbsolutePackageUri(rootDirectory);
            Dictionary<string, BundleInfo> map = new Dictionary<string, BundleInfo>();
            Collection<BundleInfo> result = new Collection<BundleInfo>();
            foreach (String path in Directory.GetFiles(rootDirectory, "*.properties", SearchOption.AllDirectories))
            {
               
                FileInfo file = new FileInfo(path);
                string relPath = ToRelativePath(path, rootDirectory);
                Match match = LanguageCodeRegex.Match(file.Name);
                if (match.Success)
                {
                    string key = match.Groups["b"].Value;
                    BundleFileUrl bfu = new BundleFileUrl(rootUri, key, (LanguageCode)Enum.Parse(typeof(LanguageCode), match.Groups["c"].Value), file.Length);
                    BundleInfo item;
                    if (map.ContainsKey(key))
                        item = map[key];
                    else
                    {
                        item = new BundleInfo(bfu);
                        map.Add(key, item);
                        result.Add(item);
                    }
                    item._files[(int)bfu.Code] = bfu;
                }
            }
            return result;
        }

        private BundleInfo(BundleFileUrl url)
        {
            _files = new BundleFileUrl[4];
            _files[(int)url.Code] = url;
            for (int i = 0; i < _files.Length; i++) {
                if (null == _files[i])
                    _files[i] = new BundleFileUrl(url, (LanguageCode)i);
            }
            ;
            _packageUri = url.Package;
            _name = url.BaseName;
        }

        public static string ToNormalizedPath(string path)
        {
            if (string.IsNullOrWhiteSpace(path))
                return "";
            path = System.IO.Path.GetFullPath(path);
            string n = System.IO.Path.GetFileName(path);
            while (string.IsNullOrWhiteSpace(n))
            {
                path = System.IO.Path.GetDirectoryName(path);
                if (string.IsNullOrWhiteSpace(path))
                    return "";
                n = System.IO.Path.GetFileName(path);
            }
            return path;
        }

        public static string ToRelativePath(string sourcePath, string toPath)
        {
            if (null == sourcePath)
                throw new ArgumentNullException("sourcePath");
            if (null == toPath)
                throw new ArgumentNullException("toPath");
            if ((sourcePath = ToNormalizedPath(sourcePath)).Length == 0)
                throw new ArgumentException("Path is empty", "sourcePath");
            if ((toPath = ToNormalizedPath(toPath)).Length == 0)
                throw new ArgumentException("Path is empty", "toPath");
            int len = toPath.Length;
            if (len == sourcePath.Length)
            {
                if (string.Equals(sourcePath, toPath, StringComparison.InvariantCultureIgnoreCase))
                    return "";
            }
            else if (len < sourcePath.Length && string.Equals(sourcePath, toPath, StringComparison.InvariantCultureIgnoreCase) && toPath[len] == '\\')
                return sourcePath.Substring(len + 1);

            throw new ArgumentException("Source path refers to a location outside the \"to\" path.", "sourcePath");
        }

        public class BundleFileUrl : PackageItemUrl
        {
            private readonly string _baseName;
            private readonly LanguageCode _code;
            private readonly long? _length;

            public long? Length { get { return _length; } }

            public LanguageCode Code { get { return _code; } }

            public string BaseName { get { return _baseName; } }

            internal BundleFileUrl(Uri root, string packageName, string baseName, LanguageCode code, long length) : base(root, packageName)
            {
                _baseName = baseName;
                _code = code;
                _length = length;
            }

            internal BundleFileUrl(BundleFileUrl url, long length) : base(url)
            {
                _baseName = url._baseName;
                _code = url._code;
                _length = length;
            }

            internal BundleFileUrl(BundleFileUrl url, LanguageCode code) : base(url)
            {
                _baseName = url._baseName;
                _code = code;
                _length = null;
            }

            public override bool IsPackage()
            {
                return false;
            }

            public override Uri GetFullResourceUri()
            {
                return new Uri(GetFullPackageUri(), _baseName + "_" + _code.ToString() + ".properties");
            }
        }
    }
    
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
            string packageRoot = Path.GetFileName(rootDirectory);
            using (StreamWriter writer = new StreamWriter(Path.Combine(dirName, fileName + "ResourceKeys.java"), false, new UTF8Encoding(false, false)))
            {
                writer.Write("package ");
                writer.Write(packageRoot);
                IEnumerator<string> enumerator = elements.GetEnumerator();
                while (enumerator.MoveNext())
                {
                    writer.Write(".");
                    writer.Write(enumerator.Current);
                }
                writer.WriteLine(";");
                writer.WriteLine("");
                writer.WriteLine("/**");
                writer.Write(" * Defines resource bundle keys for the {@code ");
                writer.Write(packageRoot);
                writer.Write("/");
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