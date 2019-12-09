using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.IO;
using System.Linq;
using System.Text;
using System.Text.RegularExpressions;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace ResourceHelper
{
    public partial class Form1 : Form
    {
        private string _loadedPath = null;
        private object _en = null;
        private PropertiesFile _de = null;
        private PropertiesFile _hi = null;
        private PropertiesFile _es = null;

        public PropertiesFile EN
        {
            get
            {
                if (_en == null)
                {
                    if (baseNameTextBox.Text.Trim().Length == 0)
                        return null;
                    string path;
                    if ((path = rootFolderTextBox.Text).Trim().Length == 0)
                        path = baseNameTextBox.Text;
                    else
                        path = Path.Combine(path, baseNameTextBox.Text + "_en.properties");
                    try { _en = PropertiesFile.Load(path); }
                    catch (Exception ex) { _en = ex; }
                }
                return (_en is PropertiesFile) ? (PropertiesFile)_en : null;
            }
        }

        public PropertiesFile DE { get { return _de; } }

        public PropertiesFile HI { get { return _hi; } }

        public PropertiesFile ES { get { return _es; } }

        public Exception LoadError { get { return (EN == null) ? (Exception)_en : null; } }
        
        public Form1()
        {
            InitializeComponent();
        }

        private void rootFolderTextBox_TextChanged(object sender, EventArgs e)
        {
            _loadedPath = null;
            _en = _de = _hi = _es = null;
            deStatusLabel.Text = hiStatusLabel.Text = esStatusLabel.Text = "Unmodified";
            saveButton.Enabled = false;
        }

        private void baseNameTextBox_TextChanged(object sender, EventArgs e)
        {
            _loadedPath = null;
            _en = _de = _hi = _es = null;
            deStatusLabel.Text = hiStatusLabel.Text = esStatusLabel.Text = "Unmodified";
            saveButton.Enabled = false;
        }

        private void rootDirBrowseButton_Click(object sender, EventArgs e)
        {

        }

        private static Regex WhiteSpace = new Regex(@"[\s\r\n]+");

        private static Regex NewLine = new Regex(@"\r\n?|\n");

        private PropertiesFile GetTranslated(string language)
        {
            PropertiesFile en = EN;
            if (en == null)
            {
                if (LoadError == null)
                    MessageBox.Show(this, "Base name not set", "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
                else
                    MessageBox.Show(this, LoadError.Message, "Open Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
                return null;
            }

            string[] keys = en.Keys.ToArray();
            string source = String.Join(Environment.NewLine, en.Keys.Select(k => WhiteSpace.Replace(String.Join(" ", en[k].ToArray()), " ").Trim()));
            Clipboard.SetText(source);
            if (MessageBox.Show(this, "Translate text in clipboard to " + language + " and click \"OK\" to continue.", "Translate", MessageBoxButtons.OKCancel, MessageBoxIcon.Information) != DialogResult.OK)
                return null;
            if (!Clipboard.ContainsText())
            {
                MessageBox.Show(this, "Clipboard contains no text", "Import Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
                return null;
            }

            string[] lines = NewLine.Split(Clipboard.GetText());
            while (lines.Length != keys.Length)
            {
                if (MessageBox.Show(this, keys.Length.ToString() + " lines expected; Actual: " + lines.Length.ToString() + ". Retry?", "Parse Error", MessageBoxButtons.YesNo, MessageBoxIcon.Error) != DialogResult.Yes)
                    return null;
                if (!Clipboard.ContainsText())
                {
                    MessageBox.Show(this, "Clipboard contains no text", "Import Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
                    return null;
                }
                lines = NewLine.Split(Clipboard.GetText());
            }

            PropertiesFile result = new PropertiesFile();
            for (int i = 0; i < keys.Length; i++)
                result.Add(new PropertiesFile.Node(keys[i], lines[i]));
            return result;
        }

        private void deSetButton_Click(object sender, EventArgs e)
        {
            PropertiesFile result = GetTranslated("German");
            if (result == null)
                return;
            _de = result;
            deStatusLabel.Text = "Updated";
            if (saveButton.Enabled)
            {
                saveButton.Enabled = false;
                _hi = _es = null;
                hiStatusLabel.Text = esStatusLabel.Text = "Unmodified";
            }
            else
                saveButton.Enabled = _hi != null && _es != null;
        }

        private void hiSetButton_Click(object sender, EventArgs e)
        {
            PropertiesFile result = GetTranslated("Hindi");
            if (result == null)
                return;
            _hi = result;
            hiStatusLabel.Text = "Updated";
            if (saveButton.Enabled)
            {
                saveButton.Enabled = false;
                _de = _es = null;
                deStatusLabel.Text = esStatusLabel.Text = "Unmodified";
            }
            else
                saveButton.Enabled = _de != null && _es != null;
        }

        private void esSetButton_Click(object sender, EventArgs e)
        {
            PropertiesFile result = GetTranslated("Spanish");
            if (result == null)
                return;
            _es = result;
            esStatusLabel.Text = "Updated";
            if (saveButton.Enabled)
            {
                saveButton.Enabled = false;
                _hi = _de = null;
                hiStatusLabel.Text = deStatusLabel.Text = "Unmodified";
            }
            else
                saveButton.Enabled = _hi != null && _de != null;
        }

        private void saveButton_Click(object sender, EventArgs e)
        {
            saveButton.Enabled = false;
            deStatusLabel.Text = hiStatusLabel.Text = esStatusLabel.Text = "Saved";
            string basePath = rootFolderTextBox.Text;
            if (basePath.Trim().Length == 0)
                basePath = ".";
            string path = Path.Combine(basePath, baseNameTextBox.Text + "_en.properties");
            try { EN.AsSorted().Save(path); }
            catch (Exception ex)
            {
                MessageBox.Show(this, "Error saving " + path + "\r\n" + ex.Message, "File Save Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
            path = Path.Combine(basePath, baseNameTextBox.Text + "_de.properties");
            try
            {
                DE.AsSorted().Save(path);
                deStatusLabel.Text = "Saved";
            }
            catch (Exception ex)
            {
                deStatusLabel.Text = "Error saving " + path + "\r\n" + ex.Message;
                MessageBox.Show(this, deStatusLabel.Text, "File Save Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
            path = Path.Combine(basePath, baseNameTextBox.Text + "_hi.properties");
            try
            {
                HI.AsSorted().Save(path);
                hiStatusLabel.Text = "Saved";
            }
            catch (Exception ex)
            {
                hiStatusLabel.Text = "Error saving " + path + "\r\n" + ex.Message;
                MessageBox.Show(this, hiStatusLabel.Text, "File Save Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
            path = Path.Combine(basePath, baseNameTextBox.Text + "_es.properties");
            try
            {
                ES.AsSorted().Save(path);
                esStatusLabel.Text = "Saved";
            }
            catch (Exception ex)
            {
                esStatusLabel.Text = "Error saving " + path + "\r\n" + ex.Message;
                MessageBox.Show(this, esStatusLabel.Text, "File Save Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
        }

        private void deResetButton_Click(object sender, EventArgs e)
        {
            _de = null;
            deStatusLabel.Text = "Unmodified";
            saveButton.Enabled = false;
        }

        private void hiResetButton_Click(object sender, EventArgs e)
        {
            _hi = null;
            hiStatusLabel.Text = "Unmodified";
            saveButton.Enabled = false;
        }

        private void esResetButton_Click(object sender, EventArgs e)
        {
            _es = null;
            esStatusLabel.Text = "Unmodified";
            saveButton.Enabled = false;
        }

        private void resetAllButton_Click(object sender, EventArgs e)
        {
            _loadedPath = null;
            _en = _de = _hi = _es = null;
            deStatusLabel.Text = hiStatusLabel.Text = esStatusLabel.Text = "Unmodified";
            saveButton.Enabled = false;
        }
    }
}
