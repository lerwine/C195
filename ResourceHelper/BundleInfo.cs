using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.IO;
using System.Text.RegularExpressions;

namespace ResourceHelper
{
    public partial class BundleInfo
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
                string relPath = ToRelativePath(file.DirectoryName, rootDirectory);
                Match match = LanguageCodeRegex.Match(file.Name);
                if (match.Success)
                {
                    string key = match.Groups["b"].Value;
                    BundleFileUrl bfu = new BundleFileUrl(rootUri, relPath, key, (LanguageCode)Enum.Parse(typeof(LanguageCode), match.Groups["c"].Value), file.Length);
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
            else if (len < sourcePath.Length && string.Equals(sourcePath.Substring(0, len), toPath, StringComparison.InvariantCultureIgnoreCase) && sourcePath[len] == '\\')
                return sourcePath.Substring(len + 1);

            throw new ArgumentException("Source path refers to a location outside the \"to\" path.", "sourcePath");
        }
    }
}