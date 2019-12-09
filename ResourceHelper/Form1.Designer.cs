namespace ResourceHelper
{
    partial class Form1
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.tableLayoutPanel1 = new System.Windows.Forms.TableLayoutPanel();
            this.label1 = new System.Windows.Forms.Label();
            this.rootFolderTextBox = new System.Windows.Forms.TextBox();
            this.rootDirBrowseButton = new System.Windows.Forms.Button();
            this.label2 = new System.Windows.Forms.Label();
            this.baseNameTextBox = new System.Windows.Forms.TextBox();
            this.tableLayoutPanel2 = new System.Windows.Forms.TableLayoutPanel();
            this.label3 = new System.Windows.Forms.Label();
            this.deStatusLabel = new System.Windows.Forms.Label();
            this.deSetButton = new System.Windows.Forms.Button();
            this.deResetButton = new System.Windows.Forms.Button();
            this.label5 = new System.Windows.Forms.Label();
            this.hiStatusLabel = new System.Windows.Forms.Label();
            this.hiSetButton = new System.Windows.Forms.Button();
            this.hiResetButton = new System.Windows.Forms.Button();
            this.label7 = new System.Windows.Forms.Label();
            this.esStatusLabel = new System.Windows.Forms.Label();
            this.esSetButton = new System.Windows.Forms.Button();
            this.esResetButton = new System.Windows.Forms.Button();
            this.saveButton = new System.Windows.Forms.Button();
            this.resetAllButton = new System.Windows.Forms.Button();
            this.tableLayoutPanel1.SuspendLayout();
            this.tableLayoutPanel2.SuspendLayout();
            this.SuspendLayout();
            // 
            // tableLayoutPanel1
            // 
            this.tableLayoutPanel1.ColumnCount = 3;
            this.tableLayoutPanel1.ColumnStyles.Add(new System.Windows.Forms.ColumnStyle());
            this.tableLayoutPanel1.ColumnStyles.Add(new System.Windows.Forms.ColumnStyle(System.Windows.Forms.SizeType.Percent, 50F));
            this.tableLayoutPanel1.ColumnStyles.Add(new System.Windows.Forms.ColumnStyle());
            this.tableLayoutPanel1.Controls.Add(this.label1, 0, 0);
            this.tableLayoutPanel1.Controls.Add(this.rootFolderTextBox, 1, 0);
            this.tableLayoutPanel1.Controls.Add(this.rootDirBrowseButton, 2, 0);
            this.tableLayoutPanel1.Controls.Add(this.label2, 0, 1);
            this.tableLayoutPanel1.Controls.Add(this.baseNameTextBox, 1, 1);
            this.tableLayoutPanel1.Controls.Add(this.tableLayoutPanel2, 0, 2);
            this.tableLayoutPanel1.Dock = System.Windows.Forms.DockStyle.Fill;
            this.tableLayoutPanel1.Location = new System.Drawing.Point(0, 0);
            this.tableLayoutPanel1.Name = "tableLayoutPanel1";
            this.tableLayoutPanel1.RowCount = 3;
            this.tableLayoutPanel1.RowStyles.Add(new System.Windows.Forms.RowStyle());
            this.tableLayoutPanel1.RowStyles.Add(new System.Windows.Forms.RowStyle());
            this.tableLayoutPanel1.RowStyles.Add(new System.Windows.Forms.RowStyle(System.Windows.Forms.SizeType.Percent, 50F));
            this.tableLayoutPanel1.Size = new System.Drawing.Size(800, 450);
            this.tableLayoutPanel1.TabIndex = 0;
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(3, 0);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(65, 13);
            this.label1.TabIndex = 0;
            this.label1.Text = "Root Folder:";
            // 
            // rootFolderTextBox
            // 
            this.rootFolderTextBox.Dock = System.Windows.Forms.DockStyle.Top;
            this.rootFolderTextBox.Location = new System.Drawing.Point(74, 3);
            this.rootFolderTextBox.Name = "rootFolderTextBox";
            this.rootFolderTextBox.Size = new System.Drawing.Size(692, 20);
            this.rootFolderTextBox.TabIndex = 1;
            this.rootFolderTextBox.Text = "C:\\Users\\webmaster\\source\\repos\\C195\\Scheduler\\src\\globalization";
            this.rootFolderTextBox.TextChanged += new System.EventHandler(this.rootFolderTextBox_TextChanged);
            // 
            // rootDirBrowseButton
            // 
            this.rootDirBrowseButton.Location = new System.Drawing.Point(772, 3);
            this.rootDirBrowseButton.Name = "rootDirBrowseButton";
            this.rootDirBrowseButton.Size = new System.Drawing.Size(25, 23);
            this.rootDirBrowseButton.TabIndex = 2;
            this.rootDirBrowseButton.Text = "...";
            this.rootDirBrowseButton.UseVisualStyleBackColor = true;
            this.rootDirBrowseButton.Click += new System.EventHandler(this.rootDirBrowseButton_Click);
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Location = new System.Drawing.Point(3, 29);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(65, 13);
            this.label2.TabIndex = 3;
            this.label2.Text = "Base Name:";
            // 
            // baseNameTextBox
            // 
            this.tableLayoutPanel1.SetColumnSpan(this.baseNameTextBox, 2);
            this.baseNameTextBox.Dock = System.Windows.Forms.DockStyle.Top;
            this.baseNameTextBox.Location = new System.Drawing.Point(74, 32);
            this.baseNameTextBox.Name = "baseNameTextBox";
            this.baseNameTextBox.Size = new System.Drawing.Size(723, 20);
            this.baseNameTextBox.TabIndex = 4;
            this.baseNameTextBox.Text = "loginScreen";
            this.baseNameTextBox.TextChanged += new System.EventHandler(this.baseNameTextBox_TextChanged);
            // 
            // tableLayoutPanel2
            // 
            this.tableLayoutPanel2.ColumnCount = 4;
            this.tableLayoutPanel1.SetColumnSpan(this.tableLayoutPanel2, 3);
            this.tableLayoutPanel2.ColumnStyles.Add(new System.Windows.Forms.ColumnStyle(System.Windows.Forms.SizeType.Percent, 50F));
            this.tableLayoutPanel2.ColumnStyles.Add(new System.Windows.Forms.ColumnStyle(System.Windows.Forms.SizeType.Percent, 50F));
            this.tableLayoutPanel2.ColumnStyles.Add(new System.Windows.Forms.ColumnStyle(System.Windows.Forms.SizeType.Percent, 50F));
            this.tableLayoutPanel2.ColumnStyles.Add(new System.Windows.Forms.ColumnStyle(System.Windows.Forms.SizeType.Percent, 50F));
            this.tableLayoutPanel2.Controls.Add(this.label3, 0, 0);
            this.tableLayoutPanel2.Controls.Add(this.deStatusLabel, 1, 0);
            this.tableLayoutPanel2.Controls.Add(this.deSetButton, 2, 0);
            this.tableLayoutPanel2.Controls.Add(this.deResetButton, 3, 0);
            this.tableLayoutPanel2.Controls.Add(this.label5, 0, 1);
            this.tableLayoutPanel2.Controls.Add(this.hiStatusLabel, 1, 1);
            this.tableLayoutPanel2.Controls.Add(this.hiSetButton, 2, 1);
            this.tableLayoutPanel2.Controls.Add(this.hiResetButton, 3, 1);
            this.tableLayoutPanel2.Controls.Add(this.label7, 0, 2);
            this.tableLayoutPanel2.Controls.Add(this.esStatusLabel, 1, 2);
            this.tableLayoutPanel2.Controls.Add(this.esSetButton, 2, 2);
            this.tableLayoutPanel2.Controls.Add(this.esResetButton, 3, 2);
            this.tableLayoutPanel2.Controls.Add(this.saveButton, 2, 3);
            this.tableLayoutPanel2.Controls.Add(this.resetAllButton, 3, 3);
            this.tableLayoutPanel2.Dock = System.Windows.Forms.DockStyle.Fill;
            this.tableLayoutPanel2.Location = new System.Drawing.Point(3, 58);
            this.tableLayoutPanel2.Name = "tableLayoutPanel2";
            this.tableLayoutPanel2.RowCount = 4;
            this.tableLayoutPanel2.RowStyles.Add(new System.Windows.Forms.RowStyle(System.Windows.Forms.SizeType.Percent, 25F));
            this.tableLayoutPanel2.RowStyles.Add(new System.Windows.Forms.RowStyle(System.Windows.Forms.SizeType.Percent, 25F));
            this.tableLayoutPanel2.RowStyles.Add(new System.Windows.Forms.RowStyle(System.Windows.Forms.SizeType.Percent, 25F));
            this.tableLayoutPanel2.RowStyles.Add(new System.Windows.Forms.RowStyle(System.Windows.Forms.SizeType.Percent, 25F));
            this.tableLayoutPanel2.Size = new System.Drawing.Size(794, 389);
            this.tableLayoutPanel2.TabIndex = 5;
            // 
            // label3
            // 
            this.label3.AutoSize = true;
            this.label3.Location = new System.Drawing.Point(3, 0);
            this.label3.Name = "label3";
            this.label3.Size = new System.Drawing.Size(44, 13);
            this.label3.TabIndex = 0;
            this.label3.Text = "German";
            // 
            // deStatusLabel
            // 
            this.deStatusLabel.AutoSize = true;
            this.deStatusLabel.Location = new System.Drawing.Point(201, 0);
            this.deStatusLabel.Name = "deStatusLabel";
            this.deStatusLabel.Size = new System.Drawing.Size(63, 13);
            this.deStatusLabel.TabIndex = 1;
            this.deStatusLabel.Text = "Unchanged";
            // 
            // deSetButton
            // 
            this.deSetButton.Location = new System.Drawing.Point(399, 3);
            this.deSetButton.Name = "deSetButton";
            this.deSetButton.Size = new System.Drawing.Size(75, 23);
            this.deSetButton.TabIndex = 2;
            this.deSetButton.Text = "Set";
            this.deSetButton.UseVisualStyleBackColor = true;
            this.deSetButton.Click += new System.EventHandler(this.deSetButton_Click);
            // 
            // deResetButton
            // 
            this.deResetButton.Location = new System.Drawing.Point(597, 3);
            this.deResetButton.Name = "deResetButton";
            this.deResetButton.Size = new System.Drawing.Size(75, 23);
            this.deResetButton.TabIndex = 3;
            this.deResetButton.Text = "Reset";
            this.deResetButton.UseVisualStyleBackColor = true;
            this.deResetButton.Click += new System.EventHandler(this.deResetButton_Click);
            // 
            // label5
            // 
            this.label5.AutoSize = true;
            this.label5.Location = new System.Drawing.Point(3, 97);
            this.label5.Name = "label5";
            this.label5.Size = new System.Drawing.Size(31, 13);
            this.label5.TabIndex = 4;
            this.label5.Text = "Hindi";
            // 
            // hiStatusLabel
            // 
            this.hiStatusLabel.AutoSize = true;
            this.hiStatusLabel.Location = new System.Drawing.Point(201, 97);
            this.hiStatusLabel.Name = "hiStatusLabel";
            this.hiStatusLabel.Size = new System.Drawing.Size(63, 13);
            this.hiStatusLabel.TabIndex = 5;
            this.hiStatusLabel.Text = "Unchanged";
            // 
            // hiSetButton
            // 
            this.hiSetButton.Location = new System.Drawing.Point(399, 100);
            this.hiSetButton.Name = "hiSetButton";
            this.hiSetButton.Size = new System.Drawing.Size(75, 23);
            this.hiSetButton.TabIndex = 6;
            this.hiSetButton.Text = "Set";
            this.hiSetButton.UseVisualStyleBackColor = true;
            this.hiSetButton.Click += new System.EventHandler(this.hiSetButton_Click);
            // 
            // hiResetButton
            // 
            this.hiResetButton.Location = new System.Drawing.Point(597, 100);
            this.hiResetButton.Name = "hiResetButton";
            this.hiResetButton.Size = new System.Drawing.Size(75, 23);
            this.hiResetButton.TabIndex = 7;
            this.hiResetButton.Text = "Reset";
            this.hiResetButton.UseVisualStyleBackColor = true;
            this.hiResetButton.Click += new System.EventHandler(this.hiResetButton_Click);
            // 
            // label7
            // 
            this.label7.AutoSize = true;
            this.label7.Location = new System.Drawing.Point(3, 194);
            this.label7.Name = "label7";
            this.label7.Size = new System.Drawing.Size(45, 13);
            this.label7.TabIndex = 8;
            this.label7.Text = "Spanish";
            // 
            // esStatusLabel
            // 
            this.esStatusLabel.AutoSize = true;
            this.esStatusLabel.Location = new System.Drawing.Point(201, 194);
            this.esStatusLabel.Name = "esStatusLabel";
            this.esStatusLabel.Size = new System.Drawing.Size(63, 13);
            this.esStatusLabel.TabIndex = 9;
            this.esStatusLabel.Text = "Unchanged";
            // 
            // esSetButton
            // 
            this.esSetButton.Location = new System.Drawing.Point(399, 197);
            this.esSetButton.Name = "esSetButton";
            this.esSetButton.Size = new System.Drawing.Size(75, 23);
            this.esSetButton.TabIndex = 10;
            this.esSetButton.Text = "Set";
            this.esSetButton.UseVisualStyleBackColor = true;
            this.esSetButton.Click += new System.EventHandler(this.esSetButton_Click);
            // 
            // esResetButton
            // 
            this.esResetButton.Location = new System.Drawing.Point(597, 197);
            this.esResetButton.Name = "esResetButton";
            this.esResetButton.Size = new System.Drawing.Size(75, 23);
            this.esResetButton.TabIndex = 11;
            this.esResetButton.Text = "Reset";
            this.esResetButton.UseVisualStyleBackColor = true;
            this.esResetButton.Click += new System.EventHandler(this.esResetButton_Click);
            // 
            // saveButton
            // 
            this.saveButton.Enabled = false;
            this.saveButton.Location = new System.Drawing.Point(399, 294);
            this.saveButton.Name = "saveButton";
            this.saveButton.Size = new System.Drawing.Size(75, 23);
            this.saveButton.TabIndex = 12;
            this.saveButton.Text = "Save";
            this.saveButton.UseVisualStyleBackColor = true;
            this.saveButton.Click += new System.EventHandler(this.saveButton_Click);
            // 
            // resetAllButton
            // 
            this.resetAllButton.Location = new System.Drawing.Point(597, 294);
            this.resetAllButton.Name = "resetAllButton";
            this.resetAllButton.Size = new System.Drawing.Size(75, 23);
            this.resetAllButton.TabIndex = 13;
            this.resetAllButton.Text = "Reset All";
            this.resetAllButton.UseVisualStyleBackColor = true;
            this.resetAllButton.Click += new System.EventHandler(this.resetAllButton_Click);
            // 
            // Form1
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(800, 450);
            this.Controls.Add(this.tableLayoutPanel1);
            this.Name = "Form1";
            this.Text = "Form1";
            this.tableLayoutPanel1.ResumeLayout(false);
            this.tableLayoutPanel1.PerformLayout();
            this.tableLayoutPanel2.ResumeLayout(false);
            this.tableLayoutPanel2.PerformLayout();
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.TableLayoutPanel tableLayoutPanel1;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.TextBox rootFolderTextBox;
        private System.Windows.Forms.Button rootDirBrowseButton;
        private System.Windows.Forms.Label label2;
        private System.Windows.Forms.TextBox baseNameTextBox;
        private System.Windows.Forms.TableLayoutPanel tableLayoutPanel2;
        private System.Windows.Forms.Label label3;
        private System.Windows.Forms.Label deStatusLabel;
        private System.Windows.Forms.Button deSetButton;
        private System.Windows.Forms.Button deResetButton;
        private System.Windows.Forms.Label label5;
        private System.Windows.Forms.Label hiStatusLabel;
        private System.Windows.Forms.Button hiSetButton;
        private System.Windows.Forms.Button hiResetButton;
        private System.Windows.Forms.Label label7;
        private System.Windows.Forms.Label esStatusLabel;
        private System.Windows.Forms.Button esSetButton;
        private System.Windows.Forms.Button esResetButton;
        private System.Windows.Forms.Button saveButton;
        private System.Windows.Forms.Button resetAllButton;
    }
}

