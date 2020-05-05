$Script:BaseResourcesPath = $PSScriptRoot | Join-Path -ChildPath 'Scheduler\resources';
$Script:BaseCodePath = $PSScriptRoot | Join-Path -ChildPath 'Scheduler\src';

$Script:AllLocales = @('en', 'es', 'de', 'hi');
Set-Location $PSScriptRoot;
Function Convert-RelativePathToFullPath {
    [CmdletBinding(DefaultParameterSetName = 'Resource')]
    Param(
        [Parameter(Mandatory = $true, ValueFromPipeline = $true)]
        [string[]]$InputPath,
        
        [Parameter(Mandatory = $true, ParameterSetName = 'Resource')]
        [switch]$Resource,
        
        [Parameter(Mandatory = $true, ParameterSetName = 'Code')]
        [switch]$Code
    )

    Begin {
        $BasePath = $Script:BaseResourcesPath;
        if ($Code.IsPresent) { $BasePath = $Script:BaseCodePath }
    }

    Process {
        $InputPath | ForEach-Object { $BasePath | Join-Path -ChildPath $_ }
    }
}

Function Test-PropertiesFilePath {
    [CmdletBinding(DefaultParameterSetName = 'Resource')]
    Param(
        [Parameter(Mandatory = $true, ValueFromPipeline = $true)]
        [AllowNull()]
        [AllowEmptyString()]
        [AllowEmptyCollection()]
        [string[]]$InputPath,
        
        [Parameter(ParameterSetName = 'Resource')]
        # Path is relative to the resources folder.
        [switch]$Resource,
        
        [Parameter(Mandatory = $true, ParameterSetName = 'Code')]
        # Path is relative to the code folder.
        [switch]$Code
    )
    
    Begin {
        $Success = $null;
    }

    Process {
        if ($null -eq $Success) { $Success = $true }
        if ($Success) {
            if ($null -eq $InputPath -or $InputPath.Length -eq 0) {
                $Success = $false;
            } else {
                $FullPaths = @();
                if ($Resource.IsPresent) {
                    $FullPaths = @($InputPath | ForEach-Object {
                        if ([string]::IsNullOrWhiteSpace($_)) { '' } else { $Script:BaseResourcesPath | Join-Path -ChildPath $_ }
                    });
                } else {
                    if ($Code.IsPresent) {
                        $FullPaths = @($InputPath | ForEach-Object {
                            if ([string]::IsNullOrWhiteSpace($_)) { '' } else { $Script:BaseCodePath | Join-Path -ChildPath $_ }
                        });
                    } else {
                        $FullPaths = @($InputPath | ForEach-Object {
                            if ([string]::IsNullOrWhiteSpace($_)) { '' } else { $_ }
                        });
                    }
                }
                foreach ($f  in $FullPaths) {
                    if ($f.Length -eq 0) {
                        $Success = $false;
                        break;
                    }
                    if (-not ($f | Test-Path -PathType Leaf)) {
                        if ($f | Test-Path -PathType Container) {
                            $Success = $false;
                            break;
                        }
                        $p = $f | Split-Path -Parent;
                        if ([string]::IsNullOrEmpty($p) -or -not ($p | Test-Path -PathType Container)) {
                            $Success = $false;
                            break;
                        }
                    }
                }
            }
        }
    }

    End { ($Success -eq $true) | Write-Output }
}


Function Test-PropertiesFile {
    [CmdletBinding(DefaultParameterSetName = 'Relative')]
    Param(
        [Parameter(Mandatory = $true, ValueFromPipeline = $true)]
        [AllowNull()]
        [AllowEmptyString()]
        [AllowEmptyCollection()]
        [Object[]]$InputObject
    )
    
    Begin {
        $Success = $null;
    }

    Process {
        if ($null -eq $Success) { $Success = $true }
        if ($Success) {
            if ($null -eq $InputObject -or $InputObject.Length -eq 0) {
                $Success = $false;
            } else {
                foreach ($o in $InputObject) {
                    if ($null -eq $o -or $null -eq $o.Properties -or $null -eq $o.Path -or $null -eq $o.Footer `
                        -or $o.Properties -isnot [System.Collections.Generic.Dictionary[System.String, [System.Tuple[System.String,System.String[]]]]] -or $o.Path -isnot [string] `
                        -or $o.Footer -isnot [string[]] -or -not ($o.Path | Test-PropertiesFilePath)) {
                        $Success = $false;
                        break;
                    }
                }
            }
        }
    }

    End { ($Success -eq $true) | Write-Output }
}

Add-Type -TypeDefinition @'
namespace ResourceHelper {
    using System;
    using System.IO;
    using System.Linq;
    using System.Text;
    using System.Text.RegularExpressions;
    using System.Collections.Generic;
    using System.Collections.ObjectModel;
    using System.Globalization;
    using System.Threading;

    public enum LanguageCode {
        en = 0,
        de = 1,
        es = 2,
        hi = 3
    }

    public class BundleInfo {
        public static readonly Regex LanguageCodeRegex = new Regex(@"(?<b>.+?)_(?<c>en|de|es|hi)(.properties)?$");
        private readonly string _packageUri;
        private readonly string _name;
        private readonly long[] _length = new long[] { -1L, -1L, -1L, -1L };
        private readonly Sizes _sizes;
        public string PackageUri { get { return _packageUri; } }
        
        public string Name { get { return _name; } }
        
        public string ToBaseName() { return (_packageUri.Length > 0) ? _packageUri + "/" + _name : _name; }
        
        public Uri ToFullPackageUri(string rootDirectory) {
            if (null == rootDirectory)
                throw new ArgumentNullException("rootDirectory");
            if ((rootDirectory = ToNormalizedPath(rootDirectory)).Length == 0)
                throw new ArgumentException("Root directory is empty", "rootDirectory");
            if (_packageUri.Length > 0)
                return new Uri(new Uri((rootDirectory.EndsWith("\\")) ? rootDirectory : rootDirectory + "\\"), _packageUri);
            return new Uri(rootDirectory);
        }

        public string ToPackagePath(string rootDirectory) {
            return ToFullPackageUri(rootDirectory).LocalPath;
        }
        
        public string ToRelativeUri(LanguageCode lang) { return GetBaseName() + "_" + lang.ToString() + ".properties"; }

        private Uri ToFullUri(string rootDirectory, LanguageCode lang) {
            if (null == rootDirectory)
                throw new ArgumentNullException("rootDirectory");
            if ((rootDirectory = ToNormalizedPath(rootDirectory)).Length == 0)
                throw new ArgumentException("Root directory is empty", "rootDirectory");
            return new Uri(new Uri((rootDirectory.EndsWith("\\")) ? rootDirectory : rootDirectory + "\\"),
            ((_packageUri.Length > 0) ? _packageUri + _name : _name) + "_" + lang.ToString() + ".properties";
        }

        public string GetFullUriEN(string rootDirectory) {
            return GetFullUri(rootDirectory, 
        }

        public class Sizes {
            private readonly BundleInfo _target;
            Sizes(BundleInfo target) {
                _target = target;
            }

            long this[LanguageCode key] { get { return _target._length[(int)key]; } 
        }
        public long LengthEN { get { return _lengthEN; } }

        public long LengthDE { get { return _lengthDE; } }

        public long LengthES { get { return _lengthES; } }

        public long LengthHI { get { return _lengthHI; } }

        public static Collection<BundleInfo> Create(String rootDirectory) {
            if (null == rootDirectory)
                throw new ArgumentNullException("rootDirectory");
            if ((rootDirectory = ToNormalizedPath(rootDirectory)).Length == 0)
                throw new ArgumentException("Root directory is empty", "rootDirectory");
            if (!Directory.Exists(rootDirectory))
                throw new DirectoryNotFoundException();
            Uri rootUri = new Uri(rootDirectory + "\\");
            Dictionary<string, BundleInfo> map = new Dictionary<string, BundleInfo>();
            Collection<BundleInfo> result = new Collection<BundleInfo>();
            foreach (FileInfo file in Directory.GetFiles(rootDirectory, "*.properties", SearchOption.AllDirectories)) {
                string relPath = ToRelativePath(file.FullName, rootDirectory);
                Match match = LanguageCodeRegex.Match(file.Name);
                if (match.Success) {
                    string key = match.Groups["b"].Value;
                    BundleInfo item;
                    if (map.ContainsKey(key))
                        item = map[key];
                    else {
                        item = new BundleInfo(key);
                        map.Add(key, item);
                        result.Add(item);
                    }
                    switch (match.Groups["c"].Value) {
                        case LANGUAGE_CODE_DE:
                            item._lengthDE = file.Length;
                            break;
                        case LANGUAGE_CODE_ES:
                            item._lengthES = file.Length;
                            break;
                        case LANGUAGE_CODE_HI:
                            item._lengthHI = file.Length;
                            break;
                        default:
                            item._lengthEN = file.Length;
                            break;
                    }
                }
            }
            return result;
        }
        
        private BundleInfo(string packageUri, string name) {
            _packageUri = packageUri;
            _name = name;
            _sizes = new Sizes(this);
        }

        public static string ToNormalizedPath(string path) {
            if (string.IsNullOrWhiteSpace(path))
                return "";
            path = System.IO.Path.GetFullPath(path);
            string n = System.IO.Path.GetFileName(path);
            while (string.IsNullOrWhiteSpace(n)) {
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
            if (len == sourcePath.Length) {
                if (string.Equals(sourcePath, toPath, StringComparison.InvariantCultureIgnoreCase))
                    return "";
            } else if (len < sourcePath.Length && string.Equals(sourcePath, toPath, StringComparison.InvariantCultureIgnoreCase) && toPath[len] == '\\')
                return sourcePath.Substring(len + 1);
                
            throw new ArgumentException("Source path refers to a location outside the \"to\" path.", "sourcePath");
        }
    }

    public class PropertiesFile {
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

        public void CopyFrom(PropertiesFile source, bool overwrite, bool remove) {
            if (null == source)
                throw new ArgumentNullException("source");
            if (ReferenceEquals(source, this))
                return;
            Monitor.Enter(_syncRoot);
            try {
                Monitor.Enter(source._syncRoot);
                try {
                    _path = source._path;
                    if (source._dictionary.Count == 0) {
                        if (remove)
                            _dictionary.Clear();
                        return;
                    }
                    if (remove) {
                        if (overwrite) {
                            _dictionary.Clear();
                            foreach (string k in source._dictionary.Keys.ToArray())
                                _dictionary.Add(k, source._dictionary[k]);
                            return;
                        }
                        if (_dictionary.Count > 0) {
                            foreach (string k in _dictionary.Keys.ToArray()) {
                                if (!source._dictionary.ContainsKey(k))
                                    _dictionary.Remove(k);
                            }
                        }
                    }
                    if (_dictionary.Count == 0) {
                        foreach (string k in source._dictionary.Keys.ToArray())
                            _dictionary.Add(k, source._dictionary[k]);
                        return;
                    }
                    if (overwrite) {
                        foreach (string k in source._dictionary.Keys) {
                            if (_dictionary.ContainsKey(k))
                                _dictionary[k] = source._dictionary[k];
                            else
                                _dictionary.Add(k, source._dictionary[k]);
                        }
                    } else {
                        foreach (string k in source._dictionary.Keys) {
                            if (!_dictionary.ContainsKey(k))
                                _dictionary.Add(k, source._dictionary[k]);
                        }
                    }
                }
                finally { Monitor.Exit(source._syncRoot); }
            }
            finally { Monitor.Exit(_syncRoot); }
        }

        public static string AssertValidFileDestination(string path, string argName) {
            if (null == path)
                throw new ArgumentNullException(argName);
            if ((path = ToNormalizedPath(path)).Length == 0)
                throw new ArgumentException("Path is empty", argName);
            if (Directory.Exists(path) || path.Length == System.IO.Path.GetPathRoot(path).Length)
                throw new ArgumentException("Path a directory", argName);
            if (!Directory.Exists(System.IO.Path.GetDirectoryName(path)))
                throw new ArgumentException("Parent directory not found", argName);
            return path;
        }
        public static string AssertValidFileDestination(string path) {
            return AssertValidFileDestination(path, "path");
        }
        
        public static string Escape(string value, bool asKey) {
            if (string.IsNullOrEmpty(value))
                return "";
            if (asKey) {
                switch (value) {
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
                value = EncodeKeyRegex.Replace(value, new MatchEvaluator((Match m) => {
                    if (m.Groups["c"].Success) {
                        switch (m.Groups["c"].Value) {
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
            
            return EncodeValueRegex.Replace(value, new MatchEvaluator((Match m) => {
                if (m.Groups["c"].Success) {
                    switch (m.Groups["c"].Value) {
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
        public static string Unescape(string value, out bool hasContinuation) {
            if (string.IsNullOrWhiteSpace(value)) {
                hasContinuation = false;
                return (null == value) ? "" : value;
            }
            if (value == "\\") {
                hasContinuation = true;
                return "";
            }
            bool c = false;
            string result = UnescapeRegex.Replace(value, new MatchEvaluator((Match m) => {
                if (m.Groups["c"].Success) {
                    switch (m.Groups["c"].Value) {
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
                if (m.Groups["u"].Success) {
                    return new String(new char[] { (char)int.Parse(m.Groups["u"].Value, NumberStyles.HexNumber) });
                }
                if (m.Groups["o"].Success) {
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
        public void Save(string path) {
            if (null == path) {
                if (_path.Length == 0)
                    throw new ArgumentNullException("path");
                path = _path;
            }
            path = AssertValidFileDestination(path, "path");
            using (StreamWriter writer = new StreamWriter(path, false, new UTF8Encoding(false, false))) {
                Monitor.Enter(_syncRoot);
                try {
                    List<string> keys = new List<string>();
                    foreach (string k in _dictionary.Keys)
                        keys.Add(k);
                    keys.Sort();
                    IEnumerator<string> enumerator = keys.GetEnumerator();
                    while (enumerator.MoveNext()) {
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
        public void Load(string path) {
            if (null == path) {
                if (_path.Length == 0)
                    throw new ArgumentNullException("path");
                path = _path;
            }
            path = AssertValidFileDestination(path, "path");
            Monitor.Enter(_syncRoot);
            try {
                Dictionary<string, string> result = new Dictionary<string, string>(StringComparer.InvariantCulture);
                if (File.Exists(path)) {
                    string currentKey = null;
                    int lineNumber = 0;
                    bool c;
                    foreach (string line in File.ReadAllLines(path, new UTF8Encoding(false, false))) {
                        if (null == currentKey) {
                            Match m = PropertyLineRegex.Match(line);
                            if (!m.Success) {
                                if (line.Trim().Length > 0)
                                    throw new Exception("Parse error at line " + lineNumber.ToString());
                                continue;
                            }
                            if (m.Groups["k"].Success) {
                                currentKey = Unescape(m.Groups["k"].Value, out c);
                                result.Add(currentKey, Unescape(m.Groups["v"].Value, out c));
                                if (!c)
                                    currentKey = null;
                            }
                        } else {
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
                if (null != key) {
                    Monitor.Enter(_syncRoot);
                    try {
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
                try {
                    if (_dictionary.ContainsKey(key))
                        _dictionary[key] = (null == value) ? "" : value;
                    else
                        _dictionary.Add(key, (null == value) ? "" : value);
                }
                finally { Monitor.Exit(_syncRoot); }
            }
        }

        public void Add(string key, string value) {
            Monitor.Enter(_syncRoot);
            try {
                _dictionary.Add(key, (null == value) ? "" : value);
            } finally { Monitor.Exit(_syncRoot); }
        }
        
        public void Remove(string key) {
            if (null != key) {
                Monitor.Enter(_syncRoot);
                try {
                    _dictionary.Remove(key);
                } finally { Monitor.Exit(_syncRoot); }
            }
        }

        public bool ContainsKey(string key) { return _dictionary.ContainsKey(key); }
        
        public Dictionary<string,string>.Enumerator GetEnumerator() {
            return _dictionary.GetEnumerator();
        }

    }
    public class ResourceBundle {
        public static string GetFileName(string baseName, LanguageType language) {
            switch (language) {
                case LanguageType.DE:
                    return baseName + "_de.properties";
                case LanguageType.ES:
                    return baseName + "_es.properties";
                case LanguageType.HI:
                    return baseName + "_hi.properties";
            }
            return baseName + "_en.properties";
        }

        public string BaseName { get { return _baseName; } }

        public string BasePath { get { return _basePath; } }
        
        public void Load() { Load(null, null); }
        public void Load(string rootPath, string relativeName) {
            if (null == rootPath) {
                rootPath = _basePath;
                string s = _baseName;
                if (rootPath.Length == 0 || s.Length == 0)
                    throw new ArgumentNullException("rootPath");
                rootPath = PropertiesFile.ToNormalizedPath(rootPath.Substring(0, rootPath.Length - s.Length));
                if (null == relativeName)
                    relativeName = s;
                else if (relativeName.Trim().Length == 0)
                    throw new ArgumentException("Relative name is empty", "relativeName");
            } else {
                if ((rootPath = PropertiesFile.ToNormalizedPath(rootPath)).Length == 0)
                    throw new ArgumentException("Root path is empty", "rootPath");
                if (null == relativeName) {
                    relativeName = _baseName;
                    if (relativeName.Length == 0)
                        throw new ArgumentNullException("rootPath");
                } else if (relativeName.Trim().Length == 0)
                    throw new ArgumentException("Relative name is empty", "relativeName");
            }
            string basePath = PropertiesFile.AssertValidFileDestination(Path.Combine(rootPath, relativeName), "relativeName");
            string baseName = PropertiesFile.ToRelativePath(basePath, rootPath);
            string fileName = Path.GetFileName(basePath);
            string dirName = Path.GetDirectoryName(basePath);
            PropertiesFile en = new PropertiesFile();
            en.Load(Path.Combine(dirName, GetFileName(fileName, LanguageType.EN)));
            PropertiesFile de = new PropertiesFile();
            de.Load(Path.Combine(dirName, GetFileName(fileName, LanguageType.DE)));
            PropertiesFile es = new PropertiesFile();
            es.Load(Path.Combine(dirName, GetFileName(fileName, LanguageType.ES)));
            PropertiesFile hi = new PropertiesFile();
            hi.Load(Path.Combine(dirName, GetFileName(fileName, LanguageType.HI)));
            Monitor.Enter(_syncRoot);
            try {
                _baseName = baseName;
                _basePath = basePath;
                _en.CopyFrom(en, true, true);
                _de.CopyFrom(de, true, true);
                _es.CopyFrom(es, true, true);
                _hi.CopyFrom(hi, true, true);
                foreach (string k in _en.Keys) {
                    if (!_de.ContainsKey(k))
                        _de.Add(k, "");
                    if (!_es.ContainsKey(k))
                        _es.Add(k, "");
                    if (!_hi.ContainsKey(k))
                        _hi.Add(k, "");
                }
                foreach (string k in _de.Keys) {
                    if (!_en.ContainsKey(k))
                        _en.Add(k, "");
                    if (!_es.ContainsKey(k))
                        _es.Add(k, "");
                    if (!_hi.ContainsKey(k))
                        _hi.Add(k, "");
                }
                foreach (string k in _es.Keys) {
                    if (!_en.ContainsKey(k))
                        _en.Add(k, "");
                    if (!_de.ContainsKey(k))
                        _de.Add(k, "");
                    if (!_hi.ContainsKey(k))
                        _hi.Add(k, "");
                }
                foreach (string k in _hi.Keys) {
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
        public void Save(string rootPath, string relativeName) {
            if (null == rootPath) {
                rootPath = _basePath;
                string s = _baseName;
                if (rootPath.Length == 0 || s.Length == 0)
                    throw new ArgumentNullException("rootPath");
                rootPath = PropertiesFile.ToNormalizedPath(rootPath.Substring(0, rootPath.Length - s.Length));
                if (null == relativeName)
                    relativeName = s;
                else if (relativeName.Trim().Length == 0)
                    throw new ArgumentException("Relative name is empty", "relativeName");
            } else {
                if ((rootPath = PropertiesFile.ToNormalizedPath(rootPath)).Length == 0)
                    throw new ArgumentException("Root path is empty", "rootPath");
                if (null == relativeName) {
                    relativeName = _baseName;
                    if (relativeName.Length == 0)
                        throw new ArgumentNullException("rootPath");
                } else if (relativeName.Trim().Length == 0)
                    throw new ArgumentException("Relative name is empty", "relativeName");
            }
            string basePath = PropertiesFile.AssertValidFileDestination(Path.Combine(rootPath, relativeName), "relativeName");
            string baseName = PropertiesFile.ToRelativePath(basePath, rootPath);
            string fileName = Path.GetFileName(basePath);
            string dirName = Path.GetDirectoryName(basePath);
            Monitor.Enter(_syncRoot);
            try {
                PropertiesFile en = new PropertiesFile();
                en.CopyFrom(_en, true, true);
                PropertiesFile de = new PropertiesFile();
                de.CopyFrom(_de, true, true);
                PropertiesFile es = new PropertiesFile();
                es.CopyFrom(_es, true, true);
                PropertiesFile hi = new PropertiesFile();
                hi.CopyFrom(_hi, true, true);
                
                en.Save(Path.Combine(dirName, GetFileName(fileName, LanguageType.EN)));
                de.Save(Path.Combine(dirName, GetFileName(fileName, LanguageType.DE)));
                es.Save(Path.Combine(dirName, GetFileName(fileName, LanguageType.ES)));
                hi.Save(Path.Combine(dirName, GetFileName(fileName, LanguageType.HI)));
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
        
        public void SaveCode(string rootDirectory, string relativeBasePath) {
            if (null == rootDirectory)
                    throw new ArgumentNullException("rootDirectory");
            rootDirectory = PropertiesFile.ToNormalizedPath(rootDirectory);
            if (rootDirectory.Length == 0)
                throw new ArgumentException("Root directory is empty", "rootDirectory");
            if (null == relativeBasePath) {
                relativeBasePath = _baseName;
                if (relativeBasePath.Length == 0)
                    throw new ArgumentNullException("relativeBasePath");
            } else if (relativeBasePath.Trim().Length == 0)
                throw new ArgumentException("Relative base path is empty", "relativeBasePath");

            string basePath = PropertiesFile.AssertValidFileDestination(Path.Combine(rootDirectory, relativeBasePath), "relativeBasePath");
            string baseName = PropertiesFile.ToRelativePath(basePath, rootDirectory);
            string fileName = Path.GetFileName(basePath);
            string dirName = Path.GetDirectoryName(basePath);

            List<string> elements = new List<string>();
            string p = baseName;
            do {
                elements.Add(Path.GetFileName(p));
                p = Path.GetDirectoryName(p);
            } while (null != p && p.Length > 0 && p != "." && p != "..");
            string className = elements[0];
            elements.RemoveAt(0);
            if (elements.Count > 1)
                elements.Reverse();
            string packageRoot = Path.GetFileName(rootDirectory);
            using (StreamWriter writer = new StreamWriter(Path.Combine(dirName, fileName + "ResourceKeys.java"), false, new UTF8Encoding(false, false))) {
                writer.Write("package ");
                writer.Write(packageRoot);
                IEnumerator<string> enumerator = elements.GetEnumerator();
                while (enumerator.MoveNext()) {
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
                while (enumerator.MoveNext()) {
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
        private LanguageType _language = LanguageType.EN;
        
        public ResourceBundle() {
            _current = _en;
        }

        public int Count { get { return _current.Count; } }
        
        public ICollection<string> Keys { get { return _current.Keys; } }
        
        public ICollection<string> Values { get { return _current.Values; } }
        
        public void CopyTo(string key, ResourceBundle other, bool force) {
            if (null == key)
                throw new ArgumentNullException();
            Monitor.Enter(_syncRoot);
            try {
                Monitor.Enter(other._syncRoot);
                try {
                    if (_current.ContainsKey(key)) {
                        if (other._current.ContainsKey(key)) {
                            other._en[key] = _en[key];
                            other._de[key] = _de[key];
                            other._es[key] = _es[key];
                            other._hi[key] = _hi[key];
                        } else {
                            other._en.Add(key, _en[key]);
                            other._de.Add(key, _de[key]);
                            other._es.Add(key, _es[key]);
                            other._hi.Add(key, _hi[key]);
                        }
                    } else if (force) {
                        if (other._current.ContainsKey(key)) {
                            other._en[key] = "";
                            other._de[key] = "";
                            other._es[key] = "";
                            other._hi[key] = "";
                        } else {
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

        public void MoveTo(string key, ResourceBundle other, bool force) {
            if (null == key)
                throw new ArgumentNullException();
            Monitor.Enter(_syncRoot);
            try {
                Monitor.Enter(other._syncRoot);
                try {
                    if (_current.ContainsKey(key)) {
                        if (other._current.ContainsKey(key)) {
                            other._en[key] = _en[key];
                            other._de[key] = _de[key];
                            other._es[key] = _es[key];
                            other._hi[key] = _hi[key];
                        } else {
                            other._en.Add(key, _en[key]);
                            other._de.Add(key, _de[key]);
                            other._es.Add(key, _es[key]);
                            other._hi.Add(key, _hi[key]);
                        }
                        _en.Remove(key);
                        _de.Remove(key);
                        _es.Remove(key);
                        _hi.Remove(key);
                    } else if (force) {
                        if (other._current.ContainsKey(key)) {
                            other._en[key] = "";
                            other._de[key] = "";
                            other._es[key] = "";
                            other._hi[key] = "";
                        } else {
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

        public LanguageType Language {
            get {
                return _language;
            }
            set {
                Monitor.Enter(_syncRoot);
                try {
                    _language = value;
                    switch (value) {
                        case LanguageType.DE:
                            _current = _de;
                            break;
                        case LanguageType.ES:
                            _current = _es;
                            break;
                        case LanguageType.HI:
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
                try {
                    if (_current.ContainsKey(key)) {
                        _current[key] = (null == value) ? "" : value;
                    } else {
                        _current.Add(key, (null == value) ? "" : value);
                        switch (_language) {
                            case LanguageType.DE:
                                _en.Add(key, "");
                                _es.Add(key, "");
                                _hi.Add(key, "");
                                break;
                            case LanguageType.ES:
                                _en.Add(key, "");
                                _de.Add(key, "");
                                _hi.Add(key, "");
                                break;
                            case LanguageType.HI:
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
        public string Get(string key, LanguageType language) {
            Monitor.Enter(_syncRoot);
            try { 
                if (key != null && _current.ContainsKey(key)) {
                    switch (language) {
                        case LanguageType.DE:
                            return _de[key];
                        case LanguageType.ES:
                            return _es[key];
                        case LanguageType.HI:
                            return _hi[key];
                        default:
                            return _en[key];
                    }
                }
            } finally { Monitor.Exit(_syncRoot); }
            return null;
        }
        public ICollection<string> GetValues(LanguageType language) {
            switch (language) {
                case LanguageType.DE:
                    return _de.Values;
                case LanguageType.ES:
                    return _es.Values;
                case LanguageType.HI:
                    return _hi.Values;
                default:
                    return _en.Values;
            }
        }
        public string GetPath(LanguageType language) {
            switch (language) {
                case LanguageType.DE:
                    return _de.Path;
                case LanguageType.ES:
                    return _es.Path;
                case LanguageType.HI:
                    return _hi.Path;
                default:
                    return _en.Path;
            }
        }
        public void Set(string key, string value, LanguageType language) {
            Monitor.Enter(_syncRoot);
            try { 
                if (key != null && _current.ContainsKey(key)) {
                    switch (language) {
                        case LanguageType.DE:
                            _de[key] = (null == value) ? "" : value;
                            break;
                        case LanguageType.ES:
                            _es[key] = (null == value) ? "" : value;
                            break;
                        case LanguageType.HI:
                            _hi[key] = (null == value) ? "" : value;
                            break;
                        default:
                            _en[key] = (null == value) ? "" : value;
                            break;
                    }
                } else {
                    switch (language) {
                        case LanguageType.DE:
                            _en.Add(key, "");
                            _de.Add(key, (null == value) ? "" : value);
                            _es.Add(key, "");
                            _hi.Add(key, "");
                            break;
                        case LanguageType.ES:
                            _en.Add(key, "");
                            _de.Add(key, "");
                            _es.Add(key, (null == value) ? "" : value);
                            _hi.Add(key, "");
                            break;
                        case LanguageType.HI:
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
            } finally { Monitor.Exit(_syncRoot); }
        }

        public Dictionary<string,string>.Enumerator GetEnumerator() {
            return _current.GetEnumerator();
        }

        public Dictionary<string,string>.Enumerator GetEnumerator(LanguageType language) {
            switch (language) {
                case LanguageType.DE:
                    return _de.GetEnumerator();
                case LanguageType.ES:
                    return _es.GetEnumerator();
                case LanguageType.HI:
                    return _hi.GetEnumerator();
                default:
                    return _en.GetEnumerator();
            }
        }
        
        public bool ContainsKey(string key) {
            return null != key && _current.ContainsKey(key);
        }

        public void Remove(string key) {
            if (key != null) {
                Monitor.Enter(_syncRoot);
                try {
                    if (_current.ContainsKey(key)) {
                        _en.Remove(key);
                        _de.Remove(key);
                        _es.Remove(key);
                        _hi.Remove(key);
                    }
                } finally { Monitor.Exit(_syncRoot); }
            }
        }

    }
}
'@ -ErrorAction Stop;

<#
$ResourceBundle = New-ResourceBundle -BasePath 'view\country\EditCountry' -ErrorAction Stop;
$ResourceBundle.SaveChanges();
#>

Function Get-ResourceBundlePaths {
    [CmdletBinding()]
    Param()

    $Script:BundleNameRegex = [System.Text.RegularExpressions.Regex]::new('(?<b>.+)_(en|es|de|hi)$');
    (Get-ChildItem -Path $Script:BaseResourcesPath -Filter "*.properties" -Recurse) | Select-Object -Property 'Name', 'DirectoryName', 'Length', @{
        label = 'Match';
        Expression = $Script:BundleNameRegex.Match($_.BaseName);
    }


    (((Get-ChildItem -Path $Script:BaseResourcesPath -Filter "*.properties" -Recurse) | Select-Object -Property 'Name', 'DirectoryName', 'Length',
    @{
        label = 'BaseName';
        expression = {
            $m = $Script:BundleNameRegex.Match($_.BaseName);
            if ($m.Success) {
                [ResourceHelper.PropertiesFile]::ToRelativePath(($_.DirectoryName | Join-Path -ChildPath $m.Groups['b'].Value));
            }
        }
    }) | Where-Object { $null -ne $_.BaseName } | Group-Object -Property 'BaseName') | Select-Object -Property @{
        label = 'Files';
        expression = {
            $b = $_.
            $_.Group | Select-Object -Property @{
                label = 'Language';
                expression = {
                }
            }, 'Length'
        }
    }, @{
        label = 'BaseName';
        expression = {
            [ResourceHelper.PropertiesFile]::ToRelativePath($_.Name, $Script:BaseResourcesPath);
            $p = ($_.Name | Split-Path -Parent) | Resolve-Path -Relative;
            $n = $p | Split-Path -Leaf; 
            $p = $p | Split-Path -Parent;
            $r = $_.Name | Split-Path -Leaf;
            if (-not ([string]::IsNullOrWhiteSpace($p) -or $p -eq '.')) {
                $r = $n | Join-Path -ChildPath $r;
                $n = $p | Split-Path -Leaf;
                $p = $p | Split-Path -Parent;
                while (-not ([string]::IsNullOrWhiteSpace($p) -or $p -eq '.')) {
                    $r = $n | Join-Path -ChildPath $r;
                    $n = $p | Split-Path -Leaf;
                    $p = $p | Split-Path -Parent;
                }
            }
            $r
        }
    } | ForEach-Object { $_.BaseName }
}

Function Add-ResourceBundleProperty {
    [CmdletBinding(DefaultParameterSetName = 'Load')]
    Param(
        [Parameter(Mandatory = $true, ParameterSetName = 'Load')]
        [string]$BaseName,
        
        [Parameter(Mandatory = $true, ParameterSetName = 'ResourceBundle')]
        [ResourceHelper.ResourceBundle]$ResourceBundle,
        
        [Parameter(Mandatory = $true)]
        [String]$Message,
        
        [Parameter(ParameterSetName = 'Load')]
        [switch]$NoSave,
        
        [Parameter(ParameterSetName = 'ResourceBundle')]
        [switch]$PassThru
    )

    $rb = $ResourceBundle;
    if ($PSBoundParameters.ContainsKey('Load')) {
        $rb = [ResourceHelper.ResourceBundle]::new();
        $rb.Load($Script:BaseResourcesPath, $BaseName);
    }
    $elements = ($Message -replace '[^a-zA-Z\d]+', ' ').Trim().Split(' ');
    if ($elements[0].Length -eq 1) {
        $elements[0] = $elements[0].ToLower();
    } else {
        $elements[0] = $elements[0].Substring(0, 1).ToLower() + $elements[0].Substring(1);
    }
    for ($i = 1; $i -lt $elements.Length; $i++) {
        if ($elements[$i].Length -eq 1) {
            $elements[$i] = $elements[$i].ToUpper();
        } else {
            $elements[$i] = $elements[$i].Substring(0, 1).ToUpper() + $elements[$i].Substring(1);
        }
    }
    $dflt = -join $elements;
    $key = Read-Host -Prompt "Enter key (blank to accept `"$dflt`")";
    if ([string]::IsNullOrWhiteSpace($key)) { $key = $dflt }
    [System.Windows.Clipboard]::SetText($Message);
    $de = Read-Host -Prompt 'German (blank to copy from clipboard)';
    if ([string]::IsNullOrWhiteSpace($de)) { $de = [System.Windows.Clipboard]::GetText() }
    $es = Read-Host -Prompt 'Spanish (blank to copy from clipboard)';
    if ([string]::IsNullOrWhiteSpace($es)) { $es = [System.Windows.Clipboard]::GetText() }
    $hi = Read-Host -Prompt 'Hindi (blank to copy from clipboard)';
    if ([string]::IsNullOrWhiteSpace($hi)) { $hi = [System.Windows.Clipboard]::GetText() }
    $rb.Set($key, $Message, [ResourceHelper.LanguageType]::EN);
    $rb.Set($key, $de, [ResourceHelper.LanguageType]::DE);
    $rb.Set($key, $es, [ResourceHelper.LanguageType]::ES);
    $rb.Set($key, $hi, [ResourceHelper.LanguageType]::HI);
    if ($PSBoundParameters.ContainsKey('BaseName') -and -not $NoSave.IsPresent) {
        $rb.Save();
        $rb.SaveCode($Script:BaseCodePath);
    }
    if ($PassThru.IsPresent -or $PSBoundParameters.ContainsKey('BaseName')) {
        $rb | Write-Output;
    }
}

Function Copy-ResourceBundleProperty {
    [CmdletBinding(DefaultParameterSetName = 'LoadCopy')]
    Param(
        [Parameter(Mandatory = $true, ValueFromPipeline = $true)]
        [string[]]$Key,
        
        [Parameter(Mandatory = $true, ParameterSetName = 'LoadCopy')]
        [Parameter(Mandatory = $true, ParameterSetName = 'LoadMove')]
        [string]$SourceBaseName,
        
        [Parameter(Mandatory = $true, ParameterSetName = 'LoadCopy')]
        [Parameter(Mandatory = $true, ParameterSetName = 'LoadMove')]
        [string]$TargetBaseName,
        
        [Parameter(Mandatory = $true, ParameterSetName = 'RbCopy')]
        [Parameter(Mandatory = $true, ParameterSetName = 'RbMove')]
        [ResourceHelper.ResourceBundle]$SourceBundle,
        
        [Parameter(Mandatory = $true, ParameterSetName = 'RbCopy')]
        [Parameter(Mandatory = $true, ParameterSetName = 'RbMove')]
        [ResourceHelper.ResourceBundle]$TargetBundle,
        
        [Parameter(Mandatory = $true, ParameterSetName = 'LoadMove')]
        [Parameter(Mandatory = $true, ParameterSetName = 'RbMove')]
        # Delete found keys from source
        [switch]$Move,
        
        [Parameter(ParameterSetName = 'LoadCopy')]
        [Parameter(Mandatory = $true, ParameterSetName = 'RbCopy')]
        # Do not delete found keys from source
        [switch]$Copy,
        
        # Overwrite existing values on target if found on source.
        [switch]$Overwrite,
        
        # Create empty item on target even if key not found on source.
        [switch]$Force,
        
        [Parameter(ParameterSetName = 'LoadCopy')]
        [switch]$NoSave,
        
        [Parameter(ParameterSetName = 'RbMove')]
        [Parameter(ParameterSetName = 'RbCopy')]
        [switch]$PassThru
    )
    
    Begin {
        $src = $SourceBundle;
        $target = $TargetBundle;
        if ($PSBoundParameters.ContainsKey('SourceBaseName')) {
            $src = [ResourceHelper.ResourceBundle]::new();
            $src.Load($Script:BaseResourcesPath, $SourceBaseName);
            $target = [ResourceHelper.ResourceBundle]::new();
            $target.Load($Script:BaseResourcesPath, $TargetBaseName);
        }
    }

    Process {
        if ($Move.IsPresent) {
            if ($Overwrite.IsPresent) {
                if ($Force) {
                    foreach ($k in $Key) {
                        if ($src.ContainsKey($k)) {
                            $target.Set($k, $src.Get($k, [ResourceHelper.LanguageType]::EN), [ResourceHelper.LanguageType]::EN);
                            $target.Set($k, $src.Get($k, [ResourceHelper.LanguageType]::DE), [ResourceHelper.LanguageType]::DE);
                            $target.Set($k, $src.Get($k, [ResourceHelper.LanguageType]::ES), [ResourceHelper.LanguageType]::ES);
                            $target.Set($k, $src.Get($k, [ResourceHelper.LanguageType]::HI), [ResourceHelper.LanguageType]::HI);
                            $src.Remove($k);
                        } else {
                            if (-not $target.ContainsKey($k)) { $target.Set($k, '', [ResourceHelper.LanguageType]::EN) }
                        }
                    }
                } else {
                    foreach ($k in $Key) {
                        if ($src.ContainsKey($k)) {
                            $target.Set($k, $src.Get($k, [ResourceHelper.LanguageType]::EN), [ResourceHelper.LanguageType]::EN);
                            $target.Set($k, $src.Get($k, [ResourceHelper.LanguageType]::DE), [ResourceHelper.LanguageType]::DE);
                            $target.Set($k, $src.Get($k, [ResourceHelper.LanguageType]::ES), [ResourceHelper.LanguageType]::ES);
                            $target.Set($k, $src.Get($k, [ResourceHelper.LanguageType]::HI), [ResourceHelper.LanguageType]::HI);
                            $src.Remove($k);
                        }
                    }
                }
            } else {
                if ($Force) {
                    foreach ($k in $Key) {
                        if (-not $target.ContainsKey($k)) {
                            if ($src.ContainsKey($k)) {
                                $target.Set($k, $src.Get($k, [ResourceHelper.LanguageType]::EN), [ResourceHelper.LanguageType]::EN);
                                $target.Set($k, $src.Get($k, [ResourceHelper.LanguageType]::DE), [ResourceHelper.LanguageType]::DE);
                                $target.Set($k, $src.Get($k, [ResourceHelper.LanguageType]::ES), [ResourceHelper.LanguageType]::ES);
                                $target.Set($k, $src.Get($k, [ResourceHelper.LanguageType]::HI), [ResourceHelper.LanguageType]::HI);
                                $src.Remove($k);
                            } else {
                                $target.Set($k, '', [ResourceHelper.LanguageType]::EN);
                            }
                        }
                    }
                } else {
                    foreach ($k in $Key) {
                        if ($src.ContainsKey($k)) {
                            if (-not $target.ContainsKey($k)) {
                                $target.Set($k, $src.Get($k, [ResourceHelper.LanguageType]::EN), [ResourceHelper.LanguageType]::EN);
                                $target.Set($k, $src.Get($k, [ResourceHelper.LanguageType]::DE), [ResourceHelper.LanguageType]::DE);
                                $target.Set($k, $src.Get($k, [ResourceHelper.LanguageType]::ES), [ResourceHelper.LanguageType]::ES);
                                $target.Set($k, $src.Get($k, [ResourceHelper.LanguageType]::HI), [ResourceHelper.LanguageType]::HI);
                            }
                            $src.Remove($k);
                        }
                    }
                }
            }
        } else {
            if ($Overwrite.IsPresent) {
                if ($Force) {
                    foreach ($k in $Key) {
                        if ($src.ContainsKey($k)) {
                            $target.Set($k, $src.Get($k, [ResourceHelper.LanguageType]::EN), [ResourceHelper.LanguageType]::EN);
                            $target.Set($k, $src.Get($k, [ResourceHelper.LanguageType]::DE), [ResourceHelper.LanguageType]::DE);
                            $target.Set($k, $src.Get($k, [ResourceHelper.LanguageType]::ES), [ResourceHelper.LanguageType]::ES);
                            $target.Set($k, $src.Get($k, [ResourceHelper.LanguageType]::HI), [ResourceHelper.LanguageType]::HI);
                        } else {
                            if (-not $target.ContainsKey($k)) { $target.Set($k, '', [ResourceHelper.LanguageType]::EN) }
                        }
                    }
                } else {
                    foreach ($k in $Key) {
                        if ($src.ContainsKey($k)) {
                            $target.Set($k, $src.Get($k, [ResourceHelper.LanguageType]::EN), [ResourceHelper.LanguageType]::EN);
                            $target.Set($k, $src.Get($k, [ResourceHelper.LanguageType]::DE), [ResourceHelper.LanguageType]::DE);
                            $target.Set($k, $src.Get($k, [ResourceHelper.LanguageType]::ES), [ResourceHelper.LanguageType]::ES);
                            $target.Set($k, $src.Get($k, [ResourceHelper.LanguageType]::HI), [ResourceHelper.LanguageType]::HI);
                        }
                    }
                }
            } else {
                if ($Force) {
                    foreach ($k in $Key) {
                        if (-not $target.ContainsKey($k)) {
                            if ($src.ContainsKey($k)) {
                                $target.Set($k, $src.Get($k, [ResourceHelper.LanguageType]::EN), [ResourceHelper.LanguageType]::EN);
                                $target.Set($k, $src.Get($k, [ResourceHelper.LanguageType]::DE), [ResourceHelper.LanguageType]::DE);
                                $target.Set($k, $src.Get($k, [ResourceHelper.LanguageType]::ES), [ResourceHelper.LanguageType]::ES);
                                $target.Set($k, $src.Get($k, [ResourceHelper.LanguageType]::HI), [ResourceHelper.LanguageType]::HI);
                            } else {
                                $target.Set($k, '', [ResourceHelper.LanguageType]::EN);
                            }
                        }
                    }
                } else {
                    foreach ($k in $Key) {
                        if ($src.ContainsKey($k) -and -not $target.ContainsKey($k)) {
                            $target.Set($k, $src.Get($k, [ResourceHelper.LanguageType]::EN), [ResourceHelper.LanguageType]::EN);
                            $target.Set($k, $src.Get($k, [ResourceHelper.LanguageType]::DE), [ResourceHelper.LanguageType]::DE);
                            $target.Set($k, $src.Get($k, [ResourceHelper.LanguageType]::ES), [ResourceHelper.LanguageType]::ES);
                            $target.Set($k, $src.Get($k, [ResourceHelper.LanguageType]::HI), [ResourceHelper.LanguageType]::HI);
                        }
                    }
                }
            }
        }
    }

    End {
        if ($PSBoundParameters.ContainsKey('TargetBaseName') -and -not $NoSave.IsPresent) {
            $target.Save();
            $target.SaveCode($Script:BaseCodePath);
            if ($Move.IsPresent) {
                $src.Save();
                $src.SaveCode($Script:BaseCodePath);
            }
        }
        if ($PassThru.IsPresent -or $PSBoundParameters.ContainsKey('TargetBaseName')) {
            $target | Write-Output;
        }
    }
}

Function Select-ResourceBundle {
    [CmdletBinding()]
    Param()

    Get-ResourceBundlePaths | Out-GridView -Title 'Select Resource Bundle' -OutputMode Single;
}
<#

Copy properties

('countryLoadError', 'customerLoadError', 'userLoadError') | Copy-ResourceBundleProperty -SourceBaseName 'view/Main' -TargetBaseName 'view/Overview';

Add new property

Add-ResourceBundleProperty -BaseName 'App' | Out-Null;


Select-ResourceBundle

#>
Add-ResourceBundleProperty -BaseName 'view/appointment/EditAppointment' | Out-Null;
